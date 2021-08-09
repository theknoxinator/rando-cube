package net.christopherknox.rc.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.christopherknox.rc.CategoryManager;
import net.christopherknox.rc.ItemManager;
import net.christopherknox.rc.RandoCubeController;
import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.model.Priority;
import net.christopherknox.rc.request.AddCategoryRequest;
import net.christopherknox.rc.request.EditCategoryRequest;
import net.christopherknox.rc.request.InvalidRequest;
import net.christopherknox.rc.request.MarkCompletedRequest;
import net.christopherknox.rc.request.RemoveCategoryRequest;
import net.christopherknox.rc.request.RemoveItemRequest;
import net.christopherknox.rc.request.SaveItemRequest;
import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.CategoryListResponse;
import net.christopherknox.rc.response.ItemListResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;
import java.util.stream.Stream;

import static net.christopherknox.rc.RandoCubeController.ERROR_CATEGORY_NOT_FOUND;
import static net.christopherknox.rc.RandoCubeController.ERROR_CATEGORY_REQUIRED;
import static net.christopherknox.rc.RandoCubeController.ERROR_ID_REQUIRED;
import static net.christopherknox.rc.RandoCubeController.ERROR_ITEM_REQUIRED;
import static net.christopherknox.rc.RandoCubeController.ERROR_PRIORITY_REQUIRED;
import static net.christopherknox.rc.RandoCubeController.ERROR_TITLE_REQUIRED;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class RandoCubeControllerTests extends TestBase {

    private static final String HEALTH_ENDPOINT = "/health";
    private static final String GET_CATEGORIES_ENDPOINT = "/getCategories";
    private static final String ADD_CATEGORY_ENDPOINT = "/addCategory";
    private static final String EDIT_CATEGORY_ENDPOINT = "/editCategory";
    private static final String REMOVE_CATEGORY_ENDPOINT = "/removeCategory";
    private static final String GET_RANDOM_SET_ENDPOINT = "/getRandomSet";
    private static final String GET_FULL_LIST_ENDPOINT = "/getFullList";
    private static final String GET_COMPLETED_LIST_ENDPOINT = "/getCompletedList";
    private static final String SAVE_ITEM_ENDPOINT = "/saveItem";
    private static final String REMOVE_ITEM_ENDPOINT = "/removeItem";
    private static final String MARK_COMPLETED_ENDPOINT = "/markCompleted";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryManager categoryManager;
    @MockBean
    private ItemManager itemManager;

    @Test
    public void pingHealth_ServiceIsRunning_ReceiveAliveStatusMessage() throws Exception {
        mockMvc.perform(get(HEALTH_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(RandoCubeController.HEALTH_MESSAGE)));
    }


    /* GET CATEGORIES */
    @Test
    public void getCategories_ServiceIsRunning_ReturnsValidList() throws Exception {
        final CategoryListResponse response = generateCategoryListResponse();

        when(categoryManager.getCategories()).thenReturn(response);

        mockMvc.perform(get(GET_CATEGORIES_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* ADD CATEGORY */
    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void addCategory_ValidRequest_ReturnsEmptyResponse(final String category) throws Exception {
        final AddCategoryRequest request = AddCategoryRequest.builder().category(category).build();
        final BaseResponse response = new BaseResponse();

        when(categoryManager.addCategory(category)).thenReturn(response);

        mockMvc.perform(post(ADD_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void addCategory_MissingCategory_ReturnsErrorResponse() throws Exception {
        final AddCategoryRequest request = new AddCategoryRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();

        mockMvc.perform(post(ADD_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void addCategory_EmptyCategory_ReturnsErrorResponse() throws Exception {
        final AddCategoryRequest request = AddCategoryRequest.builder().category("").build();
        final BaseResponse response = BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();

        mockMvc.perform(post(ADD_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void addCategory_MissingCategoryField_ReturnsErrorResponse() throws Exception {
        final InvalidRequest request = new InvalidRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();

        mockMvc.perform(post(ADD_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* EDIT CATEGORY */
    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void editCategory_ValidRequest_ReturnsEmptyResponse(final String category) throws Exception {
        final EditCategoryRequest request = EditCategoryRequest.builder()
            .oldCategory("Sports").newCategory(category).build();
        final BaseResponse response = new BaseResponse();

        when(categoryManager.editCategory("Sports", category)).thenReturn(response);

        mockMvc.perform(post(EDIT_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @ParameterizedTest
    @MethodSource("generateMissingCategories")
    public void editCategory_MissingParameter_ReturnsErrorResponse(final String oldCategory, final String newCategory,
        final String error) throws Exception {
        final EditCategoryRequest request = EditCategoryRequest.builder()
            .oldCategory(oldCategory).newCategory(newCategory).build();
        final BaseResponse response = BaseResponse.builder().error(error).build();

        mockMvc.perform(post(EDIT_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void editCategory_MissingFields_ReturnsErrorResponse() throws Exception {
        final InvalidRequest request = new InvalidRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_CATEGORY_NOT_FOUND + "null").build();

        mockMvc.perform(post(EDIT_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* REMOVE CATEGORY */
    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void removeCategory_ValidRequest_ReturnsEmptyResponse(final String category) throws Exception {
        final RemoveCategoryRequest request = RemoveCategoryRequest.builder().category(category).build();
        final BaseResponse response = new BaseResponse();

        when(categoryManager.removeCategory(category, null)).thenReturn(response);

        mockMvc.perform(post(REMOVE_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void removeCategory_MissingCategory_ReturnsErrorResponse() throws Exception {
        final RemoveCategoryRequest request = new RemoveCategoryRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();

        mockMvc.perform(post(REMOVE_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void removeCategory_EmptyCategory_ReturnsErrorResponse() throws Exception {
        final RemoveCategoryRequest request = RemoveCategoryRequest.builder().category("").build();
        final BaseResponse response = BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();

        mockMvc.perform(post(REMOVE_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void removeCategory_MissingFields_ReturnsErrorResponse() throws Exception {
        final InvalidRequest request = new InvalidRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_CATEGORY_REQUIRED).build();

        mockMvc.perform(post(REMOVE_CATEGORY_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* GET RANDOM SET */
    @ParameterizedTest
    @MethodSource("generateCategoryAndUseLast")
    public void getRandomSet_ValidCategoryAndUseLast_ReturnsValidSet(final String category, final boolean useLast) throws Exception {
        final ItemListResponse response = generateItemListResponse(category, null);

        when(itemManager.getRandomSet(category, useLast)).thenReturn(response);

        mockMvc.perform(get(GET_RANDOM_SET_ENDPOINT).queryParam("category", category)
                .queryParam("useLast", Boolean.toString(useLast)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_ValidCategory_ReturnsValidSet(final String category) throws Exception {
        final ItemListResponse response = generateItemListResponse(category, null);

        when(itemManager.getRandomSet(category, true)).thenReturn(response);

        mockMvc.perform(get(GET_RANDOM_SET_ENDPOINT).queryParam("category", category))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void getRandomSet_MissingCategory_ReturnsBadRequestError() throws Exception {
        mockMvc.perform(get(GET_RANDOM_SET_ENDPOINT))
            .andExpect(status().isBadRequest());
    }


    /* GET FULL LIST */
    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getFullList_ValidCategory_ReturnsValidList(final String category) throws Exception {
        final ItemListResponse response = generateItemListResponse(category, null);

        when(itemManager.getFullList(category)).thenReturn(response);

        mockMvc.perform(get(GET_FULL_LIST_ENDPOINT).queryParam("category", category))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void getFullList_MissingCategory_ReturnsValidList() throws Exception {
        final ItemListResponse response = generateItemListResponse(null, null);

        when(itemManager.getFullList(null)).thenReturn(response);

        mockMvc.perform(get(GET_FULL_LIST_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* GET COMPLETED LIST */
    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getCompletedList_ValidCategory_ReturnsValidList(final String category) throws Exception {
        final ItemListResponse response = generateItemListResponse(category, null);

        when(itemManager.getCompletedList(category)).thenReturn(response);

        mockMvc.perform(get(GET_COMPLETED_LIST_ENDPOINT).queryParam("category", category))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void getCompletedList_MissingCategory_ReturnsValidList() throws Exception {
        final ItemListResponse response = generateItemListResponse(null, null);

        when(itemManager.getCompletedList(null)).thenReturn(response);

        mockMvc.perform(get(GET_COMPLETED_LIST_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* SAVE ITEM */
    @Test
    public void saveItem_ValidRequest_ReturnsEmptyResponse() throws Exception {
        final Item requestItem = Item.builder().title("Test title").category("Book").priority(Priority.LOW).build();
        final SaveItemRequest request = SaveItemRequest.builder().item(requestItem).build();
        final BaseResponse response = new BaseResponse();

        when(itemManager.saveItem(requestItem)).thenReturn(response);

        mockMvc.perform(post(SAVE_ITEM_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void saveItem_MissingItem_ReturnsErrorResponse() throws Exception {
        final SaveItemRequest request = new SaveItemRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_ITEM_REQUIRED).build();

        mockMvc.perform(post(SAVE_ITEM_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void saveItem_MissingItemField_ReturnsErrorResponse() throws Exception {
        final InvalidRequest request = new InvalidRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_ITEM_REQUIRED).build();

        mockMvc.perform(post(SAVE_ITEM_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @ParameterizedTest
    @MethodSource("generateMissingItemParameters")
    public void saveItem_MissingItemParameter_ReturnsErrorResponse(final String title, final String category,
        final Priority priority, final String error) throws Exception {
        final Item requestItem = Item.builder().title(title).category(category).priority(priority).build();
        final SaveItemRequest request = SaveItemRequest.builder().item(requestItem).build();
        final BaseResponse response = BaseResponse.builder().error(error).build();

        mockMvc.perform(post(SAVE_ITEM_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* REMOVE ITEM */
    @Test
    public void removeItem_ValidRequest_ReturnsEmptyResponse() throws Exception {
        final Integer id = (new Random()).nextInt(1000) + 1;
        final RemoveItemRequest request = RemoveItemRequest.builder().id(id).build();
        final BaseResponse response = new BaseResponse();

        when(itemManager.removeItem(id)).thenReturn(response);

        mockMvc.perform(post(REMOVE_ITEM_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void removeItem_MissingId_ReturnsErrorResponse() throws Exception {
        final RemoveItemRequest request = new RemoveItemRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_ID_REQUIRED).build();

        mockMvc.perform(post(REMOVE_ITEM_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void removeItem_MissingIdField_ReturnsErrorResponse() throws Exception {
        final InvalidRequest request = new InvalidRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_ID_REQUIRED).build();

        mockMvc.perform(post(REMOVE_ITEM_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* MARK COMPLETED */
    @Test
    public void markCompleted_ValidRequest_ReturnsEmptyResponse() throws Exception {
        final Integer id = (new Random()).nextInt(1000) + 1;
        final MarkCompletedRequest request = MarkCompletedRequest.builder().id(id).build();
        final BaseResponse response = new BaseResponse();

        when(itemManager.markCompleted(id)).thenReturn(response);

        mockMvc.perform(post(MARK_COMPLETED_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void markCompleted_MissingId_ReturnsErrorResponse() throws Exception {
        final MarkCompletedRequest request = new MarkCompletedRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_ID_REQUIRED).build();

        mockMvc.perform(post(MARK_COMPLETED_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void markCompleted_MissingIdField_ReturnsErrorResponse() throws Exception {
        final InvalidRequest request = new InvalidRequest();
        final BaseResponse response = BaseResponse.builder().error(ERROR_ID_REQUIRED).build();

        mockMvc.perform(post(MARK_COMPLETED_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* HELPER FUNCTIONS */
    private static CategoryListResponse generateCategoryListResponse() {
        return CategoryListResponse.builder()
            .categories(generateCategories())
            .build();
    }

    private static ItemListResponse generateItemListResponse(final String category, final String error) {
        return ItemListResponse.builder()
            .items(generateItems(category))
            .error(error)
            .build();
    }

    private static Stream<Arguments> generateCategoryAndUseLast() {
        return generateCategories().stream().flatMap(c -> Stream.of(Arguments.of(c, false), Arguments.of(c, true)));
    }

    private static Stream<Arguments> generateMissingCategories() {
        return Stream.of(
            Arguments.of(null, testCategory, ERROR_CATEGORY_NOT_FOUND + "null"),
            Arguments.of("", testCategory, ERROR_CATEGORY_NOT_FOUND),
            Arguments.of(testCategory, null, ERROR_CATEGORY_REQUIRED),
            Arguments.of(testCategory, "", ERROR_CATEGORY_REQUIRED)
        );
    }

    private static Stream<Arguments> generateMissingItemParameters() {
        return Stream.of(
            Arguments.of(null, testCategory, Priority.LOW, ERROR_TITLE_REQUIRED),
            Arguments.of("", testCategory, Priority.LOW, ERROR_TITLE_REQUIRED),
            Arguments.of(testTitle, null, Priority.LOW, ERROR_CATEGORY_REQUIRED),
            Arguments.of(testTitle, "", Priority.LOW, ERROR_CATEGORY_REQUIRED),
            Arguments.of(testTitle, testCategory, null, ERROR_PRIORITY_REQUIRED)
        );
    }

    private String toJson(final Object response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(response);
        } catch (Exception e) {
            fail("Could not convert to JSON: " + response.toString(), e);
        }
        return "";
    }
}
