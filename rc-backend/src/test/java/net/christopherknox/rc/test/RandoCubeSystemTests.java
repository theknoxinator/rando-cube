package net.christopherknox.rc.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.christopherknox.rc.Constants;
import net.christopherknox.rc.DataHandler;
import net.christopherknox.rc.request.AddCategoryRequest;
import net.christopherknox.rc.request.EditCategoryRequest;
import net.christopherknox.rc.request.RemoveCategoryRequest;
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

import java.util.stream.Collectors;

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
    public void categories_FullWorkflow_AllChangesAppliedToSaveFile() throws Exception {
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

    private CategoryListResponse getCategories() throws Exception {
        return getResponse(doGet(Constants.GET_CATEGORIES_ENDPOINT), CategoryListResponse.class);
    }

    private ItemListResponse getFullList() throws Exception {
        return getResponse(doGet(Constants.GET_FULL_LIST_ENDPOINT), ItemListResponse.class);
    }

    private MvcResult doGet(final String endpoint) throws Exception {
        return mockMvc.perform(get(endpoint)).andExpect(status().isOk()).andReturn();
    }

    private MvcResult doPost(final String endpoint, final Object request) throws Exception {
        return mockMvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
            .andExpect(status().isOk()).andReturn();
    }

    private static <T> T getResponse(final MvcResult result, final Class<T> responseType) throws Exception {
        return (new ObjectMapper()).readValue(result.getResponse().getContentAsString(), responseType);
    }
}
