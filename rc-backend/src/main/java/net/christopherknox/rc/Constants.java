package net.christopherknox.rc;

public class Constants {
    public static final String HEALTH_ENDPOINT = "/health";
    public static final String RELOAD_ENDPOINT = "/reload";
    public static final String GET_CATEGORIES_ENDPOINT = "/getCategories";
    public static final String ADD_CATEGORY_ENDPOINT = "/addCategory";
    public static final String EDIT_CATEGORY_ENDPOINT = "/editCategory";
    public static final String REMOVE_CATEGORY_ENDPOINT = "/removeCategory";
    public static final String GET_RANDOM_SET_ENDPOINT = "/getRandomSet";
    public static final String GET_FULL_LIST_ENDPOINT = "/getFullList";
    public static final String GET_COMPLETED_LIST_ENDPOINT = "/getCompletedList";
    public static final String SAVE_ITEM_ENDPOINT = "/saveItem";
    public static final String REMOVE_ITEM_ENDPOINT = "/removeItem";
    public static final String MARK_COMPLETED_ENDPOINT = "/markCompleted";

    public static final String HEALTH_MESSAGE = "RandoCube is up and running!";
    public static final String RELOAD_MESSAGE = "RandoCube has reloaded from the save file!";
    public static final String ERROR_CATEGORY_REQUIRED = "Category is required for this operation";
    public static final String ERROR_CATEGORY_NOT_FOUND = "Could not find category: ";
    public static final String ERROR_CATEGORY_DUPLICATE = "Category already exists: ";
    public static final String ERROR_ID_REQUIRED = "ID is required for this operation";
    public static final String ERROR_ID_NOT_FOUND = "Could not find ID: ";
    public static final String ERROR_ITEM_REQUIRED = "Item is required for this operation";
    public static final String ERROR_PRIORITY_REQUIRED = "Priority is required for this operation";
    public static final String ERROR_TITLE_REQUIRED = "Title is required for this operation";
    public static final String ERROR_TITLE_DUPLICATE = "Title already exists: ";
}
