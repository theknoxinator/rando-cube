package net.christopherknox.rc;

import net.christopherknox.rc.request.AddCategoryRequest;
import net.christopherknox.rc.request.EditCategoryRequest;
import net.christopherknox.rc.request.MarkCompletedRequest;
import net.christopherknox.rc.request.RemoveCategoryRequest;
import net.christopherknox.rc.request.RemoveItemRequest;
import net.christopherknox.rc.request.SaveItemRequest;
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

import java.io.IOException;

@RestController
public class RandoCubeController {

    private final CategoryManager categoryManager;
    private final ItemManager itemManager;
    private final DataHandler dataHandler;

    @Autowired
    public RandoCubeController(final CategoryManager categoryManager, final ItemManager itemManager,
        final DataHandler dataHandler) {
        this.categoryManager = categoryManager;
        this.itemManager = itemManager;
        this.dataHandler = dataHandler;
    }

    @GetMapping(Constants.HEALTH_ENDPOINT)
    public String pingHealth() {
        return Constants.HEALTH_MESSAGE;
    }

    @GetMapping(Constants.RELOAD_ENDPOINT)
    public String reload() {
        try {
            dataHandler.reload();
        } catch (IOException e) {
            return e.toString();
        }
        return Constants.RELOAD_MESSAGE;
    }


    /* CATEGORY ENDPOINTS */
    @GetMapping(Constants.GET_CATEGORIES_ENDPOINT)
    public CategoryListResponse getCategories() {
        return categoryManager.getCategories();
    }

    @PostMapping(Constants.ADD_CATEGORY_ENDPOINT)
    public BaseResponse addCategory(@RequestBody final AddCategoryRequest request) {
        if (!StringUtils.hasText(request.getCategory())) {
            return BaseResponse.builder().error(Constants.ERROR_CATEGORY_REQUIRED).build();
        }
        return categoryManager.addCategory(request.getCategory());
    }

    @PostMapping(Constants.EDIT_CATEGORY_ENDPOINT)
    public BaseResponse editCategory(@RequestBody final EditCategoryRequest request) {
        if (!StringUtils.hasText(request.getOldCategory())) {
            return BaseResponse.builder().error(Constants.ERROR_CATEGORY_NOT_FOUND + request.getOldCategory()).build();
        } else if (!StringUtils.hasText(request.getNewCategory())) {
            return BaseResponse.builder().error(Constants.ERROR_CATEGORY_REQUIRED).build();
        }
        return categoryManager.editCategory(request.getOldCategory(), request.getNewCategory());
    }

    @PostMapping(Constants.REMOVE_CATEGORY_ENDPOINT)
    public BaseResponse removeCategory(@RequestBody final RemoveCategoryRequest request) {
        if (!StringUtils.hasText(request.getCategory())) {
            return BaseResponse.builder().error(Constants.ERROR_CATEGORY_REQUIRED).build();
        }
        return categoryManager.removeCategory(request.getCategory(), request.getMigrateTo());
    }


    /* ITEM ENDPOINTS */

    @GetMapping(Constants.GET_RANDOM_SET_ENDPOINT)
    public ItemListResponse getRandomSet(@RequestParam("category") final String category,
        @RequestParam(name="useLast", required=false, defaultValue="true") final boolean useLast) {
        return itemManager.getRandomSet(category, useLast);
    }

    @GetMapping(Constants.GET_FULL_LIST_ENDPOINT)
    public ItemListResponse getFullList(@RequestParam(name="category", required=false) final String category) {
        return itemManager.getFullList(category);
    }

    @GetMapping(Constants.GET_COMPLETED_LIST_ENDPOINT)
    public ItemListResponse getCompletedList(@RequestParam(name="category", required=false) final String category) {
        return itemManager.getCompletedList(category);
    }

    @PostMapping(Constants.SAVE_ITEM_ENDPOINT)
    public BaseResponse saveItem(@RequestBody final SaveItemRequest request) {
        if (request.getItem() == null) {
            return BaseResponse.builder().error(Constants.ERROR_ITEM_REQUIRED).build();
        } else if (!StringUtils.hasText(request.getItem().getTitle())) {
            return BaseResponse.builder().error(Constants.ERROR_TITLE_REQUIRED).build();
        } else if (!StringUtils.hasText(request.getItem().getCategory())) {
            return BaseResponse.builder().error(Constants.ERROR_CATEGORY_REQUIRED).build();
        } else if (request.getItem().getPriority() == null) {
            return BaseResponse.builder().error(Constants.ERROR_PRIORITY_REQUIRED).build();
        }
        return itemManager.saveItem(request.getItem(), request.getIgnoreDuplicate() != null ?
            request.getIgnoreDuplicate() : false);
    }

    @PostMapping(Constants.REMOVE_ITEM_ENDPOINT)
    public BaseResponse removeItem(@RequestBody final RemoveItemRequest request) {
        if (request.getId() == null) {
            return BaseResponse.builder().error(Constants.ERROR_ID_REQUIRED).build();
        }
        return itemManager.removeItem(request.getId());
    }

    @PostMapping(Constants.MARK_COMPLETED_ENDPOINT)
    public BaseResponse markCompleted(@RequestBody final MarkCompletedRequest request) {
        if (request.getId() == null) {
            return BaseResponse.builder().error(Constants.ERROR_ID_REQUIRED).build();
        }
        return itemManager.markCompleted(request.getId(), request.getUnmark() != null ? request.getUnmark() : false);
    }
}
