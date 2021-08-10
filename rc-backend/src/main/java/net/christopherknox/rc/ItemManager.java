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
import java.util.List;
import java.util.Optional;
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
        return ItemListResponse.builder()
            .items(new ArrayList<>())
            .build();
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

    public BaseResponse saveItem(final Item item) {
        return BaseResponse.builder().build();
    }

    public BaseResponse removeItem(final Integer id) {
        String title = id.toString();
        try {
            boolean foundItem = false;
            final List<Item> fullItems = dataHandler.getData();
            Optional<Item> itemToRemove = getItem(fullItems, id);
            if (itemToRemove.isPresent()) {
                foundItem = true;
                title = itemToRemove.get().getTitle();
                fullItems.remove(itemToRemove.get());
                dataHandler.setData(fullItems);
            }
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
            final List<Item> fullItems = dataHandler.getData();
            final List<Item> completedItems = dataHandler.getHistory();
            if (!unmark) {
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
}
