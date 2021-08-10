package net.christopherknox.rc.test;

import net.christopherknox.rc.CategoryManager;
import net.christopherknox.rc.DataHandler;
import net.christopherknox.rc.RandoCubeController;
import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.CategoryListResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CategoryManagerTests extends TestBase {

    @Autowired
    private CategoryManager categoryManager;

    @MockBean
    private DataHandler dataHandler;

    /* GET CATEGORIES */
    @Test
    public void getCategories_CategoriesExist_ReturnsValidList() {
        when(dataHandler.getCategories()).thenReturn(generateCategories());

        final CategoryListResponse response = categoryManager.getCategories();
        assertEquals(exampleCategories, response.getCategories());
        assertNull(response.getError());
    }

    @Test
    public void getCategories_NoCategoriesExist_ReturnsEmptyList() {
        final List<String> categories = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(new ArrayList<>());

        final CategoryListResponse response = categoryManager.getCategories();
        assertEquals(categories, response.getCategories());
        assertNull(response.getError());
    }

    @Test
    public void getCategories_DataError_ReturnsError() {
        when(dataHandler.getCategories()).thenThrow(new NullPointerException("Test"));

        final CategoryListResponse response = categoryManager.getCategories();
        assertNull(response.getCategories());
        assertEquals("Could not get categories, check logs: Test", response.getError());
    }


    /* ADD CATEGORY */
    @Test
    public void addCategory_CategoriesExist_ListUpdated() throws Exception {
        final List<String> expected = generateCategories();
        expected.add(testCategory);

        when(dataHandler.getCategories()).thenReturn(generateCategories());

        final BaseResponse response = categoryManager.addCategory(testCategory);
        assertNull(response.getError());
        verify(dataHandler).setCategories(expected);
        verify(dataHandler).save();
    }

    @Test
    public void addCategory_NoCategoriesExist_ListUpdated() throws Exception {
        final List<String> expected = new ArrayList<>();
        expected.add(testCategory);

        when(dataHandler.getCategories()).thenReturn(new ArrayList<>());

        final BaseResponse response = categoryManager.addCategory(testCategory);
        assertNull(response.getError());
        verify(dataHandler).setCategories(expected);
        verify(dataHandler).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void addCategory_DuplicateCategory_ReturnsError(final String category) throws Exception {
        final String duplicateCategory = category.toLowerCase();

        when(dataHandler.getCategories()).thenReturn(generateCategories());

        final BaseResponse response = categoryManager.addCategory(duplicateCategory);
        assertEquals(RandoCubeController.ERROR_CATEGORY_DUPLICATE + duplicateCategory, response.getError());
        verify(dataHandler, never()).setCategories(anyList());
        verify(dataHandler, never()).save();
    }

    @Test
    public void addCategory_DataError_ReturnsError() throws Exception {
        when(dataHandler.getCategories()).thenReturn(new ArrayList<>());
        doThrow(new IOException("Test")).when(dataHandler).save();

        final BaseResponse response = categoryManager.addCategory(testCategory);
        assertEquals("Could not add category: " + testCategory + ", check logs: Test", response.getError());
    }


    /* EDIT CATEGORY */
    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void editCategory_CategoryItemsAndLastSetExists_CategoryItemsAndLastSetUpdated(final String category) throws Exception {
        final List<String> expectedCategories = generateCategories();
        expectedCategories.replaceAll(c -> c.equals(category) ? testCategory : c);
        final List<Item> expectedItems = generateItems();
        expectedItems.forEach(i -> {
            if (i.getCategory().equals(category)) {
                i.setCategory(testCategory);
            }
        });
        final Map<String, List<Item>> expectedLastSets = generateLastSets();
        expectedLastSets.remove(category);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getLastSets()).thenReturn(generateLastSets());

        final BaseResponse response = categoryManager.editCategory(category.toLowerCase(), testCategory);
        assertNull(response.getError());
        verify(dataHandler).setCategories(expectedCategories);
        verify(dataHandler).setData(expectedItems);
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void editCategory_CategoryExistsNoItemsOrLastSet_OnlyCategoryUpdated(final String category) throws Exception {
        final List<String> expectedCategories = generateCategories();
        expectedCategories.replaceAll(c -> c.equals(category) ? testCategory : c);
        final List<Item> expectedItems =
            generateItems().stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        final Map<String, List<Item>> expectedLastSets = generateLastSets();
        expectedLastSets.remove(category);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        final List<Item> mockedItems =
            generateItems().stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        when(dataHandler.getData()).thenReturn(mockedItems);
        when(dataHandler.getLastSets()).thenReturn(generateLastSets(mockedItems));

        final BaseResponse response = categoryManager.editCategory(category.toLowerCase(), testCategory);
        assertNull(response.getError());
        verify(dataHandler).setCategories(expectedCategories);
        verify(dataHandler).setData(expectedItems);
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
    }

    @Test
    public void editCategory_NoCategoriesExist_ReturnsError() throws Exception {
        final String category = getRandomCategory();

        when(dataHandler.getCategories()).thenReturn(new ArrayList<>());
        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getLastSets()).thenReturn(new HashMap<>());

        final BaseResponse response = categoryManager.editCategory(category, testCategory);
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + category, response.getError());
        verify(dataHandler, never()).setCategories(anyList());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setLastSets(anyMap());
        verify(dataHandler, never()).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void editCategory_CategoryDoesNotExist_ReturnsError(final String category) throws Exception {
        final List<String> mockedCategories =
            generateCategories().stream().filter(c -> !c.equals(category)).collect(Collectors.toList());
        when(dataHandler.getCategories()).thenReturn(mockedCategories);
        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getLastSets()).thenReturn(new HashMap<>());

        final BaseResponse response = categoryManager.editCategory(category, testCategory);
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + category, response.getError());
        verify(dataHandler, never()).setCategories(anyList());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setLastSets(anyMap());
        verify(dataHandler, never()).save();
    }

    @Test
    public void editCategory_NewCategoryExists_ReturnsError() throws Exception {
        final List<String> mockedCategories = generateCategories();
        mockedCategories.add(testCategory);

        when(dataHandler.getCategories()).thenReturn(mockedCategories);
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getLastSets()).thenReturn(generateLastSets());

        final BaseResponse response = categoryManager.editCategory(getRandomCategory(), testCategory.toUpperCase());
        assertEquals(RandoCubeController.ERROR_CATEGORY_DUPLICATE + testCategory.toUpperCase(), response.getError());
        verify(dataHandler, never()).setCategories(anyList());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setLastSets(anyMap());
        verify(dataHandler, never()).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void editCategory_OldNewCategoriesSame_ReturnsError(final String category) throws Exception {
        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getLastSets()).thenReturn(generateLastSets());

        final BaseResponse response = categoryManager.editCategory(category.toLowerCase(), category.toUpperCase());
        assertEquals(RandoCubeController.ERROR_CATEGORY_DUPLICATE + category.toUpperCase(), response.getError());
        verify(dataHandler, never()).setCategories(anyList());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setLastSets(anyMap());
        verify(dataHandler, never()).save();
    }

    @Test
    public void editCategory_DataError_ReturnsError() throws Exception {
        final String category = getRandomCategory();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getLastSets()).thenReturn(generateLastSets());
        doThrow(new IOException("Test")).when(dataHandler).save();

        final BaseResponse response = categoryManager.editCategory(category, testCategory);
        assertEquals("Could not edit category: " + category + ", check logs: Test", response.getError());
    }


    /* REMOVE CATEGORY */
    @ParameterizedTest
    @MethodSource("generateCategoryAndMigrateTo")
    public void removeCategory_CategoryItemsAndLastSetsExistsMigrate_CategoryRemovedItemsMigrated(final String category,
        final String migrateTo) throws Exception {
        final List<String> expectedCategories =
            generateCategories().stream().filter(c -> !c.equals(category)).collect(Collectors.toList());
        final List<Item> expectedItems = generateItems();
        expectedItems.forEach(i -> {
            if (i.getCategory().equals(category)) {
                i.setCategory(migrateTo);
            }
        });
        final Map<String, List<Item>> expectedLastSets = generateLastSets();
        expectedLastSets.remove(category);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getLastSets()).thenReturn(generateLastSets());

        final BaseResponse response = categoryManager.removeCategory(category.toLowerCase(), migrateTo.toUpperCase());
        assertNull(response.getError());
        verify(dataHandler).setCategories(expectedCategories);
        verify(dataHandler).setData(expectedItems);
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void removeCategory_CategoryItemsAndLastSetsExistsNoMigrate_CategoryAndItemsRemoved(final String category) throws Exception {
        final List<String> expectedCategories =
            generateCategories().stream().filter(c -> !c.equals(category)).collect(Collectors.toList());
        final List<Item> expectedItems =
            generateItems().stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        final Map<String, List<Item>> expectedLastSets = generateLastSets();
        expectedLastSets.remove(category);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getLastSets()).thenReturn(generateLastSets());

        final BaseResponse response = categoryManager.removeCategory(category.toLowerCase(), null);
        assertNull(response.getError());
        verify(dataHandler).setCategories(expectedCategories);
        verify(dataHandler).setData(expectedItems);
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryAndMigrateTo")
    public void removeCategory_CategoryExistsNoItemsOrLastSetMigrate_OnlyCategoryRemoved(final String category,
        final String migrateTo) throws Exception {
        final List<String> expectedCategories =
            generateCategories().stream().filter(c -> !c.equals(category)).collect(Collectors.toList());
        final List<Item> expectedItems =
            generateItems().stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        final Map<String, List<Item>> expectedLastSets = generateLastSets();
        expectedLastSets.remove(category);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        final List<Item> mockedItems =
            generateItems().stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        when(dataHandler.getData()).thenReturn(mockedItems);
        when(dataHandler.getLastSets()).thenReturn(generateLastSets(mockedItems));

        final BaseResponse response = categoryManager.removeCategory(category.toLowerCase(), migrateTo.toUpperCase());
        assertNull(response.getError());
        verify(dataHandler).setCategories(expectedCategories);
        verify(dataHandler).setData(expectedItems);
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void removeCategory_CategoryExistsNoItemsOrLastSetNoMigrate_OnlyCategoryRemoved(final String category) throws Exception {
        final List<String> expectedCategories =
            generateCategories().stream().filter(c -> !c.equals(category)).collect(Collectors.toList());
        final List<Item> expectedItems =
            generateItems().stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        final Map<String, List<Item>> expectedLastSets = generateLastSets();
        expectedLastSets.remove(category);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        final List<Item> mockedItems =
            generateItems().stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        when(dataHandler.getData()).thenReturn(mockedItems);
        when(dataHandler.getLastSets()).thenReturn(generateLastSets(mockedItems));

        final BaseResponse response = categoryManager.removeCategory(category.toLowerCase(), null);
        assertNull(response.getError());
        verify(dataHandler).setCategories(expectedCategories);
        verify(dataHandler).setData(expectedItems);
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
    }

    @Test
    public void removeCategory_NoCategoriesExist_ReturnsError() throws Exception {
        final String category = getRandomCategory();

        when(dataHandler.getCategories()).thenReturn(new ArrayList<>());
        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getLastSets()).thenReturn(new HashMap<>());

        final BaseResponse response = categoryManager.removeCategory(category, null);
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + category, response.getError());
        verify(dataHandler, never()).setCategories(anyList());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setLastSets(anyMap());
        verify(dataHandler, never()).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void removeCategory_CategoryDoesNotExist_ReturnsError(final String category) throws Exception {
        final List<String> mockedCategories =
            generateCategories().stream().filter(c -> !c.equals(category)).collect(Collectors.toList());
        when(dataHandler.getCategories()).thenReturn(mockedCategories);
        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getLastSets()).thenReturn(new HashMap<>());

        final BaseResponse response = categoryManager.removeCategory(category, null);
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + category, response.getError());
        verify(dataHandler, never()).setCategories(anyList());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setLastSets(anyMap());
        verify(dataHandler, never()).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryAndMigrateTo")
    public void removeCategory_MigrateCategoryDoesNotExist_ReturnsError(final String category, final String migrateTo) throws Exception {
        final List<String> mockedCategories =
            generateCategories().stream().filter(c -> !c.equals(migrateTo)).collect(Collectors.toList());
        when(dataHandler.getCategories()).thenReturn(mockedCategories);
        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getLastSets()).thenReturn(new HashMap<>());

        final BaseResponse response = categoryManager.removeCategory(category, migrateTo);
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + migrateTo, response.getError());
        verify(dataHandler, never()).setCategories(anyList());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setLastSets(anyMap());
        verify(dataHandler, never()).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void removeCategory_RemoveMigrateCategoriesSame_ReturnsError(final String category) throws Exception {
        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getLastSets()).thenReturn(generateLastSets());

        final BaseResponse response = categoryManager.removeCategory(category.toLowerCase(), category.toUpperCase());
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + category.toUpperCase(), response.getError());
        verify(dataHandler, never()).setCategories(anyList());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setLastSets(anyMap());
        verify(dataHandler, never()).save();
    }

    @Test
    public void removeCategory_DataError_ReturnsError() throws Exception {
        final String category = getRandomCategory();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getLastSets()).thenReturn(generateLastSets());
        doThrow(new IOException("Test")).when(dataHandler).save();

        final BaseResponse response = categoryManager.removeCategory(category, null);
        assertEquals("Could not remove category: " + category + ", check logs: Test", response.getError());
    }


    /* HELPER FUNCTIONS */
    protected static Stream<Arguments> generateCategoryAndMigrateTo() {
        List<Arguments> arguments = new ArrayList<>();
        for (int i = 0; i < exampleCategories.size(); i++) {
            arguments.add(Arguments.of(exampleCategories.get(i), exampleCategories.get((i + 1) % exampleCategories.size())));
        }
        return arguments.stream();
    }
}
