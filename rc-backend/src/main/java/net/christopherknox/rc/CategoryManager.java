package net.christopherknox.rc;

import lombok.extern.slf4j.Slf4j;
import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.CategoryListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryManager {

    private final DataHandler dataHandler;

    @Autowired
    public CategoryManager(final DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public CategoryListResponse getCategories() {
        try {
            log.info("GetCategories called");
            return CategoryListResponse.builder()
                .categories(dataHandler.getCategories())
                .build();
        } catch (Exception e) {
            log.error("Could not get categories", e);
            return CategoryListResponse.builder()
                .error("Could not get categories, check logs: " + e.getMessage())
                .build();
        }
    }

    public BaseResponse addCategory(final String category) {
        try {
            log.info("AddCategory called with: " + category);
            List<String> categories = dataHandler.getCategories();
            if (categories.stream().anyMatch(c -> c.equalsIgnoreCase(category))) {
                return BaseResponse.builder()
                    .error(Constants.ERROR_CATEGORY_DUPLICATE + category)
                    .build();
            }
            categories.add(category);
            dataHandler.setCategories(categories);
            dataHandler.save();
            return new BaseResponse();
        } catch (Exception e) {
            log.error("Could not add category: " + category, e);
            return BaseResponse.builder()
                .error("Could not add category: " + category + ", check logs: " + e.getMessage())
                .build();
        }
    }

    public BaseResponse editCategory(final String oldCategory, final String newCategory) {
        try {
            log.info("EditCategory called with: " + oldCategory + " -> " + newCategory);
            List<String> categories = dataHandler.getCategories();
            Optional<String> categoryToReplace =
                categories.stream().filter(c -> c.equalsIgnoreCase(oldCategory)).findAny();
            if (categoryToReplace.isEmpty()) {
                return BaseResponse.builder()
                    .error(Constants.ERROR_CATEGORY_NOT_FOUND + oldCategory)
                    .build();
            } else if (categories.stream().anyMatch(c -> c.equalsIgnoreCase(newCategory))) {
                return BaseResponse.builder()
                    .error(Constants.ERROR_CATEGORY_DUPLICATE + newCategory)
                    .build();
            }
            categories.replaceAll(c -> c.equals(categoryToReplace.get()) ? newCategory : c);
            dataHandler.setCategories(categories);

            // If existing items for old category exist, migrate to new category
            List<Item> items = dataHandler.getData();
            items.forEach(i -> {
                if (i.getCategory().equals(categoryToReplace.get())) {
                    i.setCategory(newCategory);
                }
            });
            dataHandler.setData(items);

            // If existing last set for old category exists, remove from cache
            Map<String, List<Item>> lastSets = dataHandler.getLastSets();
            lastSets.remove(categoryToReplace.get());
            dataHandler.setLastSets(lastSets);

            dataHandler.save();
            return new BaseResponse();
        } catch (Exception e) {
            log.error("Could not edit category: " + oldCategory, e);
            return BaseResponse.builder()
                .error("Could not edit category: " + oldCategory + ", check logs: " + e.getMessage())
                .build();

        }
    }

    public BaseResponse removeCategory(final String category, final String migrateTo) {
        try {
            log.info("RemoveCategory called with: " + category + " -> " + migrateTo);
            List<String> categories = dataHandler.getCategories();
            Optional<String> categoryToRemove =
                categories.stream().filter(c -> c.equalsIgnoreCase(category)).findAny();
            Optional<String> categoryToMigrateTo =
                categories.stream().filter(c -> c.equalsIgnoreCase(migrateTo)).findAny();
            if (categoryToRemove.isEmpty()) {
                return BaseResponse.builder()
                    .error(Constants.ERROR_CATEGORY_NOT_FOUND + category)
                    .build();
            } else if (StringUtils.hasText(migrateTo) && categoryToMigrateTo.isEmpty() ||
                categoryToMigrateTo.equals(categoryToRemove)) {
                return BaseResponse.builder()
                    .error(Constants.ERROR_CATEGORY_NOT_FOUND + migrateTo)
                    .build();
            }
            categories.remove(categoryToRemove.get());
            dataHandler.setCategories(categories);

            // If existing items for old category exist, migrate to new category if given, otherwise remove also
            List<Item> items = dataHandler.getData();
            if (categoryToMigrateTo.isPresent()) {
                items.forEach(i -> {
                    if (i.getCategory().equals(categoryToRemove.get())) {
                        i.setCategory(categoryToMigrateTo.get());
                    }
                });
            } else {
                items = items.stream().filter(i -> !i.getCategory().equals(categoryToRemove.get()))
                    .collect(Collectors.toList());
            }
            dataHandler.setData(items);

            // If existing last set for old category exists, remove from cache
            Map<String, List<Item>> lastSets = dataHandler.getLastSets();
            lastSets.remove(categoryToRemove.get());
            dataHandler.setLastSets(lastSets);

            dataHandler.save();
            return new BaseResponse();
        } catch (Exception e) {
            log.error("Could not remove category: " + category, e);
            return BaseResponse.builder()
                .error("Could not remove category: " + category + ", check logs: " + e.getMessage())
                .build();

        }
    }
}
