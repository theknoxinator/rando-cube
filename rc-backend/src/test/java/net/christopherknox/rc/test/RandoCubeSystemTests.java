package net.christopherknox.rc.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.christopherknox.rc.Constants;
import net.christopherknox.rc.DataHandler;
import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.model.Priority;
import net.christopherknox.rc.request.AddCategoryRequest;
import net.christopherknox.rc.request.EditCategoryRequest;
import net.christopherknox.rc.request.MarkCompletedRequest;
import net.christopherknox.rc.request.RemoveCategoryRequest;
import net.christopherknox.rc.request.RemoveItemRequest;
import net.christopherknox.rc.request.SaveItemRequest;
import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.CategoryListResponse;
import net.christopherknox.rc.response.ItemListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class RandoCubeSystemTests extends TestBase {

    @Autowired
    private MockMvc mockMvc;

    @Value("${data.filepath}")
    private String testFilepath;

    @Test
    public void categoryManagement_FullWorkflow_AllChangesAppliedToSaveFile() throws Exception {
        // First create the test file and reload it into the system
        DataHandler.Data testData = generateTestData();
        saveTestData(testFilepath, testData);
        mockMvc.perform(get(Constants.RELOAD_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(Constants.RELOAD_MESSAGE)));

        // Test case 1: Verify get categories returns categories loaded from file
        CategoryListResponse getCategoriesResponse = getCategories();
        assertEquals(testData.getCategories(), getCategoriesResponse.getCategories());

        // Test case 2: Add a new category, verify get categories returns it and it is saved to file
        AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder().category(testCategory).build();
        MvcResult mvcResult = doPost(Constants.ADD_CATEGORY_ENDPOINT, addCategoryRequest);
        BaseResponse addCategoryResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(addCategoryResponse.getError());

        testData.getCategories().add(testCategory);
        getCategoriesResponse = getCategories();
        assertEquals(testData.getCategories(), getCategoriesResponse.getCategories());

        DataHandler.Data savedData = getTestData(testFilepath);
        assertEquals(testData.getCategories(), savedData.getCategories());

        // Test case 3: Edit a category, verify get categories returns change and existing items are changed and it is
        // saved to file
        EditCategoryRequest editCategoryRequest = EditCategoryRequest.builder()
            .oldCategory("Books").newCategory("Books/Audiobooks").build();
        mvcResult = doPost(Constants.EDIT_CATEGORY_ENDPOINT, editCategoryRequest);
        BaseResponse editCategoryResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(editCategoryResponse.getError());

        testData.getCategories().replaceAll(c -> c.equals("Books") ? "Books/Audiobooks" : c);
        testData.getData().forEach(i -> {
            if (i.getCategory().equals("Books")) {
                i.setCategory("Books/Audiobooks");
            }
        });
        getCategoriesResponse = getCategories();
        assertEquals(testData.getCategories(), getCategoriesResponse.getCategories());
        ItemListResponse getFullListResponse = getFullList();
        assertEquals(testData.getData(), getFullListResponse.getItems());

        savedData = getTestData(testFilepath);
        assertEquals(testData.getCategories(), savedData.getCategories());
        assertEquals(testData.getData(), savedData.getData());

        // Test case 4: Remove a category and migrate to existing, verify get categories returns change and existing
        // items are changed and it is saved to file
        RemoveCategoryRequest removeCategoryRequest = RemoveCategoryRequest.builder()
            .category("Board Games").migrateTo(testCategory).build();
        mvcResult = doPost(Constants.REMOVE_CATEGORY_ENDPOINT, removeCategoryRequest);
        BaseResponse removeCategoryResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(removeCategoryResponse.getError());

        testData.getCategories().remove("Board Games");
        testData.getData().forEach(i -> {
            if (i.getCategory().equals("Board Games")) {
                i.setCategory(testCategory);
            }
        });
        getCategoriesResponse = getCategories();
        assertEquals(testData.getCategories(), getCategoriesResponse.getCategories());
        getFullListResponse = getFullList();
        assertEquals(testData.getData(), getFullListResponse.getItems());

        savedData = getTestData(testFilepath);
        assertEquals(testData.getCategories(), savedData.getCategories());
        assertEquals(testData.getData(), savedData.getData());

        // Test case 5: Remove a category and do not migrate, verify get categories returns change and existing items
        // are removed and it is saved to file
        removeCategoryRequest = RemoveCategoryRequest.builder().category("Movies/TV").build();
        mvcResult = doPost(Constants.REMOVE_CATEGORY_ENDPOINT, removeCategoryRequest);
        removeCategoryResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(removeCategoryResponse.getError());

        testData.getCategories().remove("Movies/TV");
        testData.setData(testData.getData().stream()
            .filter(i -> !i.getCategory().equals("Movies/TV"))
            .collect(Collectors.toList()));
        getCategoriesResponse = getCategories();
        assertEquals(testData.getCategories(), getCategoriesResponse.getCategories());
        getFullListResponse = getFullList();
        assertEquals(testData.getData(), getFullListResponse.getItems());

        savedData = getTestData(testFilepath);
        assertEquals(testData.getCategories(), savedData.getCategories());
        assertEquals(testData.getData(), savedData.getData());
    }

    @Test
    public void getRandomSet_FullWorkflow_ReturnsSetAndSetIsCachedToSaveFile() throws Exception {
        // First create the test file and reload it into the system
        DataHandler.Data testData = generateTestData(6);
        // We override a couple things here to make test cases more consistent
        testData.setLastSets(new HashMap<>());
        testData.setDefaultSetSize(3);
        saveTestData(testFilepath, testData);
        mockMvc.perform(get(Constants.RELOAD_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(Constants.RELOAD_MESSAGE)));

        // Test case 1: Get random set with flag true but no cache, set is random and saved to file
        final String category = getRandomCategory();
        ItemListResponse getRandomSetResponse = getRandomSet(category, true);
        assertThat(getRandomSetResponse.getItems()).allMatch(i -> i.getCategory().equals(category));

        testData.getLastSets().put(category, getRandomSetResponse.getItems());
        DataHandler.Data savedData = getTestData(testFilepath);
        assertEquals(testData.getLastSets(), savedData.getLastSets());

        // Test case 2: Get random set with flag false and cache, set is random and saved to file
        getRandomSetResponse = getRandomSet(category, false);
        assertThat(getRandomSetResponse.getItems()).allMatch(i -> i.getCategory().equals(category));

        testData.getLastSets().put(category, getRandomSetResponse.getItems());
        savedData = getTestData(testFilepath);
        assertEquals(testData.getLastSets(), savedData.getLastSets());

        // Test case 3: Get random set with flag true and cache, set is same as previous and saved to file
        getRandomSetResponse = getRandomSet(category, true);
        assertEquals(testData.getLastSets().get(category), getRandomSetResponse.getItems());

        testData.getLastSets().put(category, getRandomSetResponse.getItems());
        savedData = getTestData(testFilepath);
        assertEquals(testData.getLastSets(), savedData.getLastSets());

        // Test case 4: Get random set with flag true and cache after item is removed, set contains remaining cache
        // items and set is saved to file
        List<Item> expectedItems = testData.getLastSets().get(category);
        Item toRemove = expectedItems.remove(0);
        RemoveItemRequest removeItemRequest = RemoveItemRequest.builder().id(toRemove.getId()).build();
        MvcResult mvcResult = doPost(Constants.REMOVE_ITEM_ENDPOINT, removeItemRequest);
        BaseResponse removeItemResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(removeItemResponse.getError());

        getRandomSetResponse = getRandomSet(category, true);
        assertThat(getRandomSetResponse.getItems()).containsAll(expectedItems);

        testData.getLastSets().put(category, getRandomSetResponse.getItems());
        savedData = getTestData(testFilepath);
        assertEquals(testData.getLastSets(), savedData.getLastSets());

        // Test case 5: Get random set with flag true and cache after item is marked complete, set contains remaining
        // cache items and set is saved to file
        expectedItems = testData.getLastSets().get(category);
        Item toMark = expectedItems.remove(0);
        MarkCompletedRequest markCompletedRequest = MarkCompletedRequest.builder().id(toMark.getId()).build();
        mvcResult = doPost(Constants.MARK_COMPLETED_ENDPOINT, markCompletedRequest);
        BaseResponse markCompletedResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(markCompletedResponse.getError());

        getRandomSetResponse = getRandomSet(category, true);
        assertThat(getRandomSetResponse.getItems()).containsAll(expectedItems);

        testData.getLastSets().put(category, getRandomSetResponse.getItems());
        savedData = getTestData(testFilepath);
        assertEquals(testData.getLastSets(), savedData.getLastSets());
    }

    @Test
    public void itemManagement_FullWorkflow_AllChangesAppliedToSaveFile() throws Exception {
        // First create the test file and reload it into the system
        DataHandler.Data testData = generateTestData();
        // Move over one random item to the history
        Item toMark = testData.getData().remove((new Random()).nextInt(testData.getData().size()));
        toMark.setCompleted(LocalDate.now());
        testData.getHistory().add(toMark);
        saveTestData(testFilepath, testData);
        mockMvc.perform(get(Constants.RELOAD_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(Constants.RELOAD_MESSAGE)));

        // Test case 1: Add new item, get full list returns new item, changes saved to file
        Random rand = new Random();
        Item toAdd = Item.builder().category(getRandomCategory()).title(testTitle + " " + rand.nextInt(1000000))
            .priority(Priority.MEDIUM).added(LocalDate.now()).build();

        SaveItemRequest saveItemRequest = SaveItemRequest.builder().item(toAdd).build();
        MvcResult mvcResult = doPost(Constants.SAVE_ITEM_ENDPOINT, saveItemRequest);
        BaseResponse saveItemResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(saveItemResponse.getError());

        toAdd.setId(testData.getNextId());
        testData.getData().add(toAdd);
        testData.setNextId(testData.getNextId() + 1);

        ItemListResponse getFullListResponse = getFullList();
        assertEquals(testData.getData(), getFullListResponse.getItems());

        DataHandler.Data savedData = getTestData(testFilepath);
        assertEquals(testData.getData(), savedData.getData());
        assertEquals(testData.getNextId(), savedData.getNextId());

        // Test case 2: Edit item, get full list returns edited item, changes saved to file
        toAdd.setCategory(getRandomCategory());
        toAdd.setTitle(testTitle + " " + rand.nextInt(1000000));
        toAdd.setPriority(Priority.LOW);

        saveItemRequest = SaveItemRequest.builder().item(toAdd).build();
        mvcResult = doPost(Constants.SAVE_ITEM_ENDPOINT, saveItemRequest);
        saveItemResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(saveItemResponse.getError());

        getFullListResponse = getFullList();
        assertEquals(testData.getData(), getFullListResponse.getItems());

        savedData = getTestData(testFilepath);
        assertEquals(testData.getData(), savedData.getData());
        assertEquals(testData.getNextId(), savedData.getNextId());

        // Test case 3: Remove item, get full list returns without item, changes saved to file
        Item toRemove = testData.getData().remove(rand.nextInt(testData.getData().size()));

        RemoveItemRequest removeItemRequest = RemoveItemRequest.builder().id(toRemove.getId()).build();
        mvcResult = doPost(Constants.REMOVE_ITEM_ENDPOINT, removeItemRequest);
        BaseResponse removeItemResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(removeItemResponse.getError());

        getFullListResponse = getFullList();
        assertEquals(testData.getData(), getFullListResponse.getItems());

        savedData = getTestData(testFilepath);
        assertEquals(testData.getData(), savedData.getData());

        // Test case 4: Remove item from history, get completed list returns without item, changes saved to file
        toRemove = testData.getHistory().remove(rand.nextInt(testData.getHistory().size()));

        removeItemRequest = RemoveItemRequest.builder().id(toRemove.getId()).build();
        mvcResult = doPost(Constants.REMOVE_ITEM_ENDPOINT, removeItemRequest);
        removeItemResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(removeItemResponse.getError());

        ItemListResponse getCompletedListResponse = getCompletedList();
        assertEquals(testData.getHistory(), getCompletedListResponse.getItems());

        savedData = getTestData(testFilepath);
        assertEquals(testData.getHistory(), savedData.getHistory());
    }

    @Test
    public void markComplete_FullWorkflow_AllChangesAppliedToSaveFile() throws Exception {
        // First create the test file and reload it into the system
        DataHandler.Data testData = generateTestData(10);
        saveTestData(testFilepath, testData);
        mockMvc.perform(get(Constants.RELOAD_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(Constants.RELOAD_MESSAGE)));

        // Test case 1: Mark multiple items as completed, get completed list returns marked items, changes saved to file
        for (int count = 0; count < 5; count++) {
            String category = getRandomCategory();
            Item toMark = testData.getData().stream().filter(i -> i.getCategory().equals(category))
                .collect(Collectors.toList()).get(0);
            testData.getData().remove(toMark);
            toMark.setCompleted(LocalDate.now());
            testData.getHistory().add(toMark);

            MarkCompletedRequest markCompletedRequest = MarkCompletedRequest.builder().id(toMark.getId()).build();
            MvcResult mvcResult = doPost(Constants.MARK_COMPLETED_ENDPOINT, markCompletedRequest);
            BaseResponse markCompletedResponse = getResponse(mvcResult, BaseResponse.class);
            assertNull(markCompletedResponse.getError());

            ItemListResponse getFullListResponse = getFullList();
            assertEquals(testData.getData(), getFullListResponse.getItems());

            ItemListResponse getCompletedListResponse = getCompletedList();
            assertEquals(testData.getHistory(), getCompletedListResponse.getItems());

            DataHandler.Data savedData = getTestData(testFilepath);
            assertEquals(testData.getData(), savedData.getData());
            assertEquals(testData.getHistory(), savedData.getHistory());
        }

        // Test case 2: Unmark an item from completed list, get full list returns unmarked item, changes saved to file
        Item toUnmark = testData.getHistory().get(0);
        testData.getHistory().remove(toUnmark);
        toUnmark.setCompleted(null);
        testData.getData().add(toUnmark);

        MarkCompletedRequest markCompletedRequest =
            MarkCompletedRequest.builder().id(toUnmark.getId()).unmark(Boolean.TRUE).build();
        MvcResult mvcResult = doPost(Constants.MARK_COMPLETED_ENDPOINT, markCompletedRequest);
        BaseResponse markCompletedResponse = getResponse(mvcResult, BaseResponse.class);
        assertNull(markCompletedResponse.getError());

        ItemListResponse getFullListResponse = getFullList();
        assertEquals(testData.getData(), getFullListResponse.getItems());

        ItemListResponse getCompletedListResponse = getCompletedList();
        assertEquals(testData.getHistory(), getCompletedListResponse.getItems());

        DataHandler.Data savedData = getTestData(testFilepath);
        assertEquals(testData.getData(), savedData.getData());
        assertEquals(testData.getHistory(), savedData.getHistory());
    }

    private CategoryListResponse getCategories() throws Exception {
        return getResponse(doGet(Constants.GET_CATEGORIES_ENDPOINT), CategoryListResponse.class);
    }

    private ItemListResponse getFullList() throws Exception {
        return getResponse(doGet(Constants.GET_FULL_LIST_ENDPOINT), ItemListResponse.class);
    }

    private ItemListResponse getCompletedList() throws Exception {
        return getResponse(doGet(Constants.GET_COMPLETED_LIST_ENDPOINT), ItemListResponse.class);
    }

    private ItemListResponse getRandomSet(final String category, final boolean useLast) throws Exception {
        MultiValueMap<String, String> params = new MultiValueMapAdapter<>(Map.of(
            "category", List.of(category),
            "useLast", List.of(Boolean.toString(useLast))
        ));
        return getResponse(doGet(Constants.GET_RANDOM_SET_ENDPOINT, params), ItemListResponse.class);
    }

    private MvcResult doGet(final String endpoint) throws Exception {
        return mockMvc.perform(get(endpoint)).andExpect(status().isOk()).andReturn();
    }

    private MvcResult doGet(final String endpoint, final MultiValueMap<String, String> params) throws Exception {
        return mockMvc.perform(get(endpoint).queryParams(params)).andExpect(status().isOk()).andReturn();
    }

    private MvcResult doPost(final String endpoint, final Object request) throws Exception {
        return mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk()).andReturn();
    }

    private static <T> T getResponse(final MvcResult result, final Class<T> responseType) throws Exception {
        return (new ObjectMapper()).readValue(result.getResponse().getContentAsString(), responseType);
    }
}
