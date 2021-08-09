package net.christopherknox.rc;

import net.christopherknox.rc.request.AddCategoryRequest;
import net.christopherknox.rc.request.EditCategoryRequest;
import net.christopherknox.rc.request.RemoveCategoryRequest;
import net.christopherknox.rc.request.SaveItemRequest;
import net.christopherknox.rc.request.MarkCompletedRequest;
import net.christopherknox.rc.request.RemoveItemRequest;
import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.CategoryListResponse;
import net.christopherknox.rc.response.ItemListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RandoCubeController {
    public static final String HEALTH_MESSAGE = "RandoCube is up and running!";
    public static final String ERROR_CATEGORY_REQUIRED = "Category is required for this operation";
    public static final String ERROR_CATEGORY_NOT_FOUND = "Could not find category: ";
    public static final String ERROR_CATEGORY_DUPLICATE = "Category already exists: ";
    public static final String ERROR_ID_REQUIRED = "ID is required for this operation";
    public static final String ERROR_ITEM_REQUIRED = "Item is required for this operation";
    public static final String ERROR_PRIORITY_REQUIRED = "Priority is required for this operation";
    public static final String ERROR_TITLE_REQUIRED = "Title is required for this operation";

    private final CategoryManager categoryManager;
    private final ItemManager itemManager;

    @Autowired
    public RandoCubeController(final CategoryManager categoryManager, final ItemManager itemManager) {
        this.categoryManager = categoryManager;
        this.itemManager = itemManager;
    }

    @GetMapping("/health")
    public String pingHealth() {
        return HEALTH_MESSAGE;
    }


    /* CATEGORY ENDPOINTS */
    @GetMapping("/getCategories")
    public CategoryListResponse getCategories() {
        return categoryManager.getCategories();
    }

    @PostMapping("/addCategory")
    public BaseResponse addCategory(@RequestBody final AddCategoryRequest request) {
        if (!StringUtils.hasText(request.getCategory())) {
            return BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();
        }
        return categoryManager.addCategory(request.getCategory());
    }

    @PostMapping("/editCategory")
    public BaseResponse editCategory(@RequestBody final EditCategoryRequest request) {
        if (!StringUtils.hasText(request.getOldCategory())) {
            return BaseResponse.builder().error(ERROR_CATEGORY_NOT_FOUND + request.getOldCategory()).build();
        } else if (!StringUtils.hasText(request.getNewCategory())) {
            return BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();
        }
        return categoryManager.editCategory(request.getOldCategory(), request.getNewCategory());
    }

    @PostMapping("/removeCategory")
    public BaseResponse removeCategory(@RequestBody final RemoveCategoryRequest request) {
        if (!StringUtils.hasText(request.getCategory())) {
            return BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();
        }
        return categoryManager.removeCategory(request.getCategory(), request.getMigrateTo());
    }


    /* ITEM ENDPOINTS */

    @GetMapping("/getRandomSet")
    public ItemListResponse getRandomSet(@RequestParam("category") final String category,
        @RequestParam(name="useLast", required=false, defaultValue="true") final boolean useLast) {
        return itemManager.getRandomSet(category, useLast);
    }

    @GetMapping("/getFullList")
    public ItemListResponse getFullList(@RequestParam(name="category", required=false) final String category) {
        return itemManager.getFullList(category);
    }

    @GetMapping("/getCompletedList")
    public ItemListResponse getCompletedList(@RequestParam(name="category", required=false) final String category) {
        return itemManager.getCompletedList(category);
    }

    @PostMapping("/saveItem")
    public BaseResponse saveItem(@RequestBody final SaveItemRequest request) {
        if (request.getItem() == null) {
            return BaseResponse.builder().error(ERROR_ITEM_REQUIRED).build();
        } else if (!StringUtils.hasText(request.getItem().getTitle())) {
            return BaseResponse.builder().error(ERROR_TITLE_REQUIRED).build();
        } else if (!StringUtils.hasText(request.getItem().getCategory())) {
            return BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();
        } else if (request.getItem().getPriority() == null) {
            return BaseResponse.builder().error(ERROR_PRIORITY_REQUIRED).build();
        }
        return itemManager.saveItem(request.getItem());
    }

    @PostMapping("/removeItem")
    public BaseResponse removeItem(@RequestBody final RemoveItemRequest request) {
        if (request.getId() == null) {
            return BaseResponse.builder().error(ERROR_ID_REQUIRED).build();
        }
        return itemManager.removeItem(request.getId());
    }

    @PostMapping("/markCompleted")
    public BaseResponse markCompleted(@RequestBody final MarkCompletedRequest request) {
        if (request.getId() == null) {
            return BaseResponse.builder().error(ERROR_ID_REQUIRED).build();
        }
        return itemManager.markCompleted(request.getId());
    }
}
