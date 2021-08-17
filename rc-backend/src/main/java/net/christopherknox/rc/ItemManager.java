package net.christopherknox.rc;

import lombok.extern.slf4j.Slf4j;
import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.ItemListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemManager {

    private final DataHandler dataHandler;

    @Autowired
    public ItemManager(final DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public ItemListResponse getRandomSet(final String category, final boolean useLast) {
        try {
            final ItemListResponse response = getItemList(dataHandler.getData(), category);
            if (response.getError() != null) {
                return response;
            }

            // Use the priorities of each item to determine how many times they show up in the weighted list
            List<Item> weightedItems = new ArrayList<>();
            for (Item item : response.getItems()) {
                for (int i = 0; i < item.getPriority().getWeight(); i++) {
                    weightedItems.add(item);
                }
            }

            // With our weighted list, now select a set of unique items
            final int setSize = Math.min(dataHandler.getDefaultSetSize(), response.getItems().size());
            final Set<Item> itemsToReturn = new HashSet<>();
            final Map<String, List<Item>> lastSets = dataHandler.getLastSets();
            if (useLast) {
                // Flag for use last is set, so grab the last set of options for this category if they exist and put
                // them in the set first
                final List<Item> lastSet = lastSets.getOrDefault(category, new ArrayList<>());
                for (Item item : lastSet) {
                    if (weightedItems.contains(item)) {
                        itemsToReturn.add(item);
                    }
                }
            }
            final Random rand = new Random(System.currentTimeMillis());
            while (itemsToReturn.size() < setSize) {
                itemsToReturn.add(weightedItems.get(rand.nextInt(weightedItems.size())));
            }

            lastSets.put(category, new ArrayList<>(itemsToReturn));
            dataHandler.setLastSets(lastSets);
            dataHandler.save();
            response.setItems(new ArrayList<>(itemsToReturn));
            return response;
        } catch (Exception e) {
            log.error("Could not get random set for: " + category, e);
            return ItemListResponse.builder()
                .error("Could not get random set for: " + category + ", check logs: " + e.getMessage())
                .build();
        }
    }

    public ItemListResponse getFullList(final String category) {
        try {
            return getItemList(dataHandler.getData(), category);
        } catch (Exception e) {
            log.error("Could not get items", e);
            return ItemListResponse.builder()
                .error("Could not get items, check logs: " + e.getMessage())
                .build();
        }
    }

    public ItemListResponse getCompletedList(final String category) {
        try {
            return getItemList(dataHandler.getHistory(), category);
        } catch (Exception e) {
            log.error("Could not get completed items", e);
            return ItemListResponse.builder()
                .error("Could not get completed items, check logs: " + e.getMessage())
                .build();
        }
    }

    public BaseResponse saveItem(final Item item, final boolean ignoreDuplicate) {
        try {
            Item itemToSave = new Item();

            // First check to see if this is an update (ID already exists)
            final List<Item> fullItems = dataHandler.getData();
            if (item.getId() != null) {
                Optional<Item> existingItemToSave = getItem(fullItems, item.getId());
                if (existingItemToSave.isEmpty()) {
                    return BaseResponse.builder()
                        .error(RandoCubeController.ERROR_ID_NOT_FOUND + item.getId())
                        .build();
                }
                itemToSave = existingItemToSave.get();
            }

            // Next check to make sure the category is valid
            Optional<String> category =
                dataHandler.getCategories().stream().filter(c -> c.equalsIgnoreCase(item.getCategory())).findAny();
            if (category.isEmpty()) {
                return BaseResponse.builder()
                    .error(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + item.getCategory())
                    .build();
            }

            // Next check to make sure the title is not a duplicate (unless flag is set, in which case skip check)
            if (!ignoreDuplicate) {
                List<Item> duplicates = new ArrayList<>();
                duplicates.addAll(getAllByTitle(fullItems, item.getTitle()));
                duplicates.addAll(getAllByTitle(dataHandler.getHistory(), item.getTitle()));
                if (!duplicates.isEmpty()) {
                    Optional<Item> matchesId = duplicates.stream().filter(i -> i.getId().equals(item.getId())).findAny();
                    if (matchesId.isEmpty()) {
                        return BaseResponse.builder()
                            .error(RandoCubeController.ERROR_TITLE_DUPLICATE + item.getTitle())
                            .build();
                    }
                }
            }

            // All checks done, so save the needed data into item
            itemToSave.setCategory(category.get());
            itemToSave.setTitle(item.getTitle());
            itemToSave.setPriority(item.getPriority());

            // Finally, if this is a new item, need to give it an ID and add to the to-do list
            if (itemToSave.getId() == null) {
                itemToSave.setId(dataHandler.getNextId());
                itemToSave.setAdded(LocalDate.now());
                fullItems.add(itemToSave);
            }

            dataHandler.setData(fullItems);
            dataHandler.save();
            return new BaseResponse();
        } catch (Exception e) {
            log.error("Could not save item", e);
            return BaseResponse.builder()
                .error("Could not save item: " + item.getTitle() + ", check logs: " + e.getMessage())
                .build();
        }
    }

    public BaseResponse removeItem(final Integer id) {
        String title = id.toString();
        try {
            boolean foundItem = false;

            // First check for the item in the to-do list
            final List<Item> fullItems = dataHandler.getData();
            Optional<Item> itemToRemove = getItem(fullItems, id);
            if (itemToRemove.isPresent()) {
                foundItem = true;
                title = itemToRemove.get().getTitle();
                fullItems.remove(itemToRemove.get());
                dataHandler.setData(fullItems);
            }

            // Also check for the item in the completed list if it's not in the to-do list
            final List<Item> completedItems = dataHandler.getHistory();
            itemToRemove = getItem(completedItems, id);
            if (itemToRemove.isPresent()) {
                foundItem = true;
                title = itemToRemove.get().getTitle();
                completedItems.remove(itemToRemove.get());
                dataHandler.setHistory(completedItems);
            }

            if (!foundItem) {
                return BaseResponse.builder()
                    .error(RandoCubeController.ERROR_ID_NOT_FOUND + id)
                    .build();
            }

            dataHandler.save();
            return new BaseResponse();
        } catch (Exception e) {
            log.error("Could not remove item", e);
            return BaseResponse.builder()
                .error("Could not remove item: " + title + ", check logs: " + e.getMessage())
                .build();
        }
    }

    public BaseResponse markCompleted(final Integer id, final boolean unmark) {
        String title = id.toString();
        try {
            boolean foundItem = false;

            // Get both to-do and completed lists since both will be changed
            final List<Item> fullItems = dataHandler.getData();
            final List<Item> completedItems = dataHandler.getHistory();
            if (!unmark) {
                // Flag is not set, so we are marking as completed, moving from to-do list to completed list (history)
                Optional<Item> itemToMark = getItem(fullItems, id);
                if (itemToMark.isPresent()) {
                    foundItem = true;
                    Item marked = itemToMark.get();
                    title = marked.getTitle();
                    fullItems.remove(marked);
                    marked.setCompleted(LocalDate.now());
                    completedItems.add(marked);
                }
            } else {
                // Flag is set, so we are marking as not completed, moving back to to-do list from completed list
                Optional<Item> itemToUnmark = getItem(completedItems, id);
                if (itemToUnmark.isPresent()) {
                    foundItem = true;
                    Item unmarked = itemToUnmark.get();
                    title = unmarked.getTitle();
                    completedItems.remove(unmarked);
                    unmarked.setCompleted(null);
                    fullItems.add(unmarked);
                }
            }

            if (!foundItem) {
                return BaseResponse.builder()
                    .error(RandoCubeController.ERROR_ID_NOT_FOUND + id)
                    .build();
            }

            dataHandler.setData(fullItems);
            dataHandler.setHistory(completedItems);
            dataHandler.save();
            return new BaseResponse();
        } catch (Exception e) {
            log.error("Could not mark/unmark item", e);
            return BaseResponse.builder()
                .error("Could not mark/unmark item: " + title + ", check logs: " + e.getMessage())
                .build();
        }
    }


    /* HELPER FUNCTIONS */
    private ItemListResponse getItemList(final List<Item> items, final String category) {
        if (!StringUtils.hasText(category)) {
            return ItemListResponse.builder()
                .items(items)
                .build();
        }
        Optional<String> categoryToFilter =
            dataHandler.getCategories().stream().filter(c -> c.equalsIgnoreCase(category)).findAny();
        if (categoryToFilter.isEmpty()) {
            return ItemListResponse.builder()
                .error(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + category)
                .build();
        }
        return ItemListResponse.builder()
            .items(items.stream().filter(i ->
                i.getCategory().equals(categoryToFilter.get())).collect(Collectors.toList()))
            .build();
    }

    private Optional<Item> getItem(final List<Item> items, final Integer id) {
        return items.stream().filter(i -> i.getId().equals(id)).findFirst();
    }

    private List<Item> getAllByTitle(final List<Item> items, final String title) {
        return items.stream()
            .filter(i -> i.getTitle().replaceAll("//s", "").equalsIgnoreCase(title.replaceAll("//s", "")))
            .collect(Collectors.toList());
    }
}
