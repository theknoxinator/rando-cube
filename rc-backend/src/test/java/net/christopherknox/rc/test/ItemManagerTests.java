package net.christopherknox.rc.test;

import net.christopherknox.rc.DataHandler;
import net.christopherknox.rc.ItemManager;
import net.christopherknox.rc.RandoCubeController;
import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.model.Priority;
import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.ItemListResponse;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemManagerTests extends TestBase {

    @Autowired
    private ItemManager itemManager;

    @MockBean
    private DataHandler dataHandler;

    /* GET RANDOM SET */
    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_CategoryBiggerThanSetSize_ReturnsRandomSubset(final String category) throws Exception {
        final Integer setSize = (new Random()).nextInt(4) + 2;
        final int categorySize = 10;
        final List<Item> expected = generateItems(category, categorySize);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems(categorySize));
        when(dataHandler.getLastSets()).thenReturn(new HashMap<>());
        when(dataHandler.getDefaultSetSize()).thenReturn(setSize);

        final ItemListResponse response = itemManager.getRandomSet(category, true);
        final Map<String, List<Item>> expectedLastSets = generateLastSets(response.getItems());
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
        assertEquals(setSize, response.getItems().size());
        assertThat(expected).containsAll(response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_CategorySameSizeAsSetSize_ReturnsWholeCategory(final String category) throws Exception {
        final Integer setSize = (new Random()).nextInt(4) + 2;
        final List<Item> expected = generateItems(category, setSize);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems(setSize));
        when(dataHandler.getLastSets()).thenReturn(new HashMap<>());
        when(dataHandler.getDefaultSetSize()).thenReturn(setSize);

        final ItemListResponse response = itemManager.getRandomSet(category, true);
        final Map<String, List<Item>> expectedLastSets = generateLastSets(response.getItems());
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
        assertEquals(setSize, response.getItems().size());
        assertThat(expected).containsExactlyInAnyOrderElementsOf(response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_CategorySmallerThanSetSize_ReturnsWholeCategory(final String category) throws Exception {
        final Integer setSize = (new Random()).nextInt(4) + 2;
        final int categorySize = 1;
        final List<Item> expected = generateItems(category, categorySize);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems(categorySize));
        when(dataHandler.getLastSets()).thenReturn(new HashMap<>());
        when(dataHandler.getDefaultSetSize()).thenReturn(setSize);

        final ItemListResponse response = itemManager.getRandomSet(category, true);
        final Map<String, List<Item>> expectedLastSets = generateLastSets(response.getItems());
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
        assertEquals(categorySize, response.getItems().size());
        assertThat(expected).containsExactlyInAnyOrderElementsOf(response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_CategoryEmpty_ReturnsEmptySet(final String category) throws Exception {
        final Integer setSize = (new Random()).nextInt(4) + 2;
        final int categorySize = 10;
        final List<Item> expected = generateItems(category, categorySize);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        final List<Item> mockedItems = generateItems(categorySize).stream()
            .filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        when(dataHandler.getData()).thenReturn(mockedItems);
        when(dataHandler.getLastSets()).thenReturn(new HashMap<>());
        when(dataHandler.getDefaultSetSize()).thenReturn(setSize);

        final ItemListResponse response = itemManager.getRandomSet(category, true);
        final Map<String, List<Item>> expectedLastSets = Maps.newHashMap(category, new ArrayList<>());
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
        assertEquals(0, response.getItems().size());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_AllLastSetItemsStillPresent_ReturnsFullLastSet(final String category) throws Exception {
        final Integer setSize = (new Random()).nextInt(4) + 2;
        final int categorySize = 10;
        final List<Item> expected = generateItems(category, categorySize).subList(0, setSize);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems(categorySize));
        final Map<String, List<Item>> mockedLastSets =
            generateLastSets(generateItems(category, categorySize).subList(0, setSize));
        when(dataHandler.getLastSets()).thenReturn(mockedLastSets);
        when(dataHandler.getDefaultSetSize()).thenReturn(setSize);

        final ItemListResponse response = itemManager.getRandomSet(category, true);
        final Map<String, List<Item>> expectedLastSets = generateLastSets(response.getItems());
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
        assertEquals(setSize, response.getItems().size());
        assertThat(expected).containsExactlyInAnyOrderElementsOf(response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_SomeLastSetItemsStillPresent_ReturnsRandomSubsetWithLastSet(final String category) throws Exception {
        final Integer setSize = (new Random()).nextInt(4) + 2;
        final int categorySize = 10;
        final List<Item> expected = generateItems(category, categorySize);
        final List<Item> expectedLastSet = expected.subList(0, setSize - 1);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems(categorySize));
        final Map<String, List<Item>> mockedLastSets =
            generateLastSets(generateItems(category, categorySize).subList(0, setSize - 1));
        when(dataHandler.getLastSets()).thenReturn(mockedLastSets);
        when(dataHandler.getDefaultSetSize()).thenReturn(setSize);

        final ItemListResponse response = itemManager.getRandomSet(category, true);
        final Map<String, List<Item>> expectedLastSets = generateLastSets(response.getItems());
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
        assertEquals(setSize, response.getItems().size());
        assertThat(response.getItems()).containsAll(expectedLastSet);
        assertThat(expected).containsAll(response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_NoLastSetItemsStillPresent_ReturnsRandomSubset(final String category) throws Exception {
        final Integer setSize = (new Random()).nextInt(4) + 2;
        final int categorySize = 10;
        final List<Item> expected = generateItems(category, categorySize);
        // Last set contains only items with an even ID number
        final List<Item> notExpectedLastSet = expected.stream()
            .filter(i -> i.getId() % 2 == 0).collect(Collectors.toList()).subList(0, setSize);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        // Mocked items contains only items with an odd ID number
        final List<Item> mockedItems =
            generateItems(categorySize).stream().filter(i -> i.getId() % 2 != 0).collect(Collectors.toList());
        when(dataHandler.getData()).thenReturn(mockedItems);
        final Map<String, List<Item>> mockedLastSets = Maps.newHashMap(category, notExpectedLastSet);
        when(dataHandler.getLastSets()).thenReturn(mockedLastSets);
        when(dataHandler.getDefaultSetSize()).thenReturn(setSize);

        final ItemListResponse response = itemManager.getRandomSet(category, true);
        final Map<String, List<Item>> expectedLastSets = generateLastSets(response.getItems());
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
        assertEquals(setSize, response.getItems().size());
        assertThat(response.getItems()).noneMatch(notExpectedLastSet::contains);
        assertThat(expected).containsAll(response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_LastSetItemsStillPresentNoFlag_ReturnsRandomSubset(final String category) throws Exception {
        final Integer setSize = (new Random()).nextInt(4) + 2;
        final int categorySize = 10;
        final List<Item> expected = generateItems(category, categorySize);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems(categorySize));
        final Map<String, List<Item>> mockedLastSets =
            generateLastSets(generateItems(category, categorySize).subList(0, setSize));
        when(dataHandler.getLastSets()).thenReturn(mockedLastSets);
        when(dataHandler.getDefaultSetSize()).thenReturn(setSize);

        final ItemListResponse response = itemManager.getRandomSet(category, false);
        final Map<String, List<Item>> expectedLastSets = generateLastSets(response.getItems());
        verify(dataHandler).setLastSets(expectedLastSets);
        verify(dataHandler).save();
        assertEquals(setSize, response.getItems().size());
        assertThat(expected).containsAll(response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getRandomSet_CategoryDoesNotExist_ReturnsError(final String category) {
        final List<String> mockedCategories =
            generateCategories().stream().filter(c -> !c.equals(category)).collect(Collectors.toList());
        when(dataHandler.getCategories()).thenReturn(mockedCategories);
        when(dataHandler.getData()).thenReturn(generateItems());

        final ItemListResponse response = itemManager.getRandomSet(category, true);
        assertNull(response.getItems());
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + category, response.getError());
    }

    @Test
    public void getRandomSet_DataError_ReturnsError() {
        final String category = getRandomCategory();

        when(dataHandler.getData()).thenThrow(new NullPointerException("Test"));

        final ItemListResponse response = itemManager.getRandomSet(category, true);
        assertNull(response.getItems());
        assertEquals("Could not get random set for: " + category + ", check logs: Test", response.getError());
    }

    /* GET FULL LIST */
    @Test
    public void getFullList_GetAllExistingItems_ReturnsFullList() {
        final List<Item> expected = generateItems();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());

        final ItemListResponse response = itemManager.getFullList(null);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @Test
    public void getFullList_GetAllNoExistingItems_ReturnsEmptyList() {
        final List<Item> expected = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(new ArrayList<>());

        final ItemListResponse response = itemManager.getFullList(null);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getFullList_GetByCategoryExistingItems_ReturnsCategoryList(final String category) {
        final List<Item> expected = generateItems(category);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());

        final ItemListResponse response = itemManager.getFullList(category);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getFullList_GetByCategoryNoExistingItems_ReturnsEmptyList(final String category) {
        final List<Item> expected = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(new ArrayList<>());

        final ItemListResponse response = itemManager.getFullList(category);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getFullList_GetByCategoryNoCategoryItems_ReturnsEmptyList(final String category) {
        final List<Item> expected = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        final List<Item> mockedItems =
            generateItems().stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        when(dataHandler.getData()).thenReturn(mockedItems);

        final ItemListResponse response = itemManager.getFullList(category);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getFullList_CategoryDoesNotExist_ReturnsError(final String category) {
        final List<String> mockedCategories =
            generateCategories().stream().filter(c -> !c.equals(category)).collect(Collectors.toList());
        when(dataHandler.getCategories()).thenReturn(mockedCategories);
        when(dataHandler.getData()).thenReturn(generateItems());

        final ItemListResponse response = itemManager.getFullList(category);
        assertNull(response.getItems());
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + category, response.getError());
    }

    @Test
    public void getFullList_DataError_ReturnsError() {
        when(dataHandler.getData()).thenThrow(new NullPointerException("Test"));

        final ItemListResponse response = itemManager.getFullList(null);
        assertNull(response.getItems());
        assertEquals("Could not get items, check logs: Test", response.getError());
    }


    /* GET COMPLETED LIST */
    @Test
    public void getCompletedList_GetAllExistingItems_ReturnsFullList() {
        final List<Item> expected = generateItems(true);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final ItemListResponse response = itemManager.getCompletedList(null);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @Test
    public void getCompletedList_GetAllNoExistingItems_ReturnsEmptyList() {
        final List<Item> expected = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final ItemListResponse response = itemManager.getCompletedList(null);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getCompletedList_GetByCategoryExistingItems_ReturnsCategoryList(final String category) {
        final List<Item> expected = generateItems(category, true);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final ItemListResponse response = itemManager.getCompletedList(category);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getCompletedList_GetByCategoryNoExistingItems_ReturnsEmptyList(final String category) {
        final List<Item> expected = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final ItemListResponse response = itemManager.getCompletedList(category);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getCompletedList_GetByCategoryNoCategoryItems_ReturnsEmptyList(final String category) {
        final List<Item> expected = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        final List<Item> mockedItems =
            generateItems(true).stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        when(dataHandler.getHistory()).thenReturn(mockedItems);

        final ItemListResponse response = itemManager.getCompletedList(category);
        assertEquals(expected, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getCompletedList_CategoryDoesNotExist_ReturnsError(final String category) {
        final List<String> mockedCategories =
            generateCategories().stream().filter(c -> !c.equals(category)).collect(Collectors.toList());
        when(dataHandler.getCategories()).thenReturn(mockedCategories);
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final ItemListResponse response = itemManager.getCompletedList(category);
        assertNull(response.getItems());
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + category, response.getError());
    }

    @Test
    public void getCompletedList_DataError_ReturnsError() {
        when(dataHandler.getHistory()).thenThrow(new NullPointerException("Test"));

        final ItemListResponse response = itemManager.getCompletedList(null);
        assertNull(response.getItems());
        assertEquals("Could not get completed items, check logs: Test", response.getError());
    }


    /* SAVE ITEM */
    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void saveItem_SaveNewItem_ListUpdated(final String category) throws Exception {
        final List<Item> expected = generateItems();
        final Integer id = expected.size() + 1;
        final Item item = Item.builder().category(category).title(testTitle).priority(Priority.MEDIUM).build();
        expected.add(item);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());
        when(dataHandler.getNextId()).thenReturn(id);

        final BaseResponse response = itemManager.saveItem(item, false);
        item.setId(id);
        item.setAdded(LocalDate.now());
        assertNull(response.getError());
        verify(dataHandler).setData(expected);
        verify(dataHandler).save();
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void saveItem_SaveExistingItem_ListUpdated(final String category) throws Exception {
        final List<Item> expected = generateItems();
        final Item item = expected.get((new Random()).nextInt(expected.size()));
        item.setCategory(category);
        item.setTitle(testTitle);
        item.setPriority(Priority.HIGH);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.saveItem(item, false);
        assertNull(response.getError());
        verify(dataHandler).setData(expected);
        verify(dataHandler).save();
    }

    @Test
    public void saveItem_SaveExistingItemNoChanges_ListUnchanged() throws Exception {
        final List<Item> expected = generateItems();
        final Item item = expected.get((new Random()).nextInt(expected.size()));

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.saveItem(item, false);
        assertNull(response.getError());
        verify(dataHandler).setData(expected);
        verify(dataHandler).save();
    }

    @Test
    public void saveItem_IdDoesNotExist_ReturnsError() throws Exception {
        final List<Item> items = generateItems();
        final Item item = items.get((new Random()).nextInt(items.size()));
        item.setId(-1);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.saveItem(item, false);
        assertEquals(RandoCubeController.ERROR_ID_NOT_FOUND + item.getId(), response.getError());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).save();
    }

    @Test
    public void saveItem_CategoryDoesNotExist_ReturnsError() throws Exception {
        final Item item = Item.builder().category(testCategory).title(testTitle).priority(Priority.MEDIUM).build();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.saveItem(item, false);
        assertEquals(RandoCubeController.ERROR_CATEGORY_NOT_FOUND + item.getCategory(), response.getError());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).save();
    }

    @Test
    public void saveItem_TitleAlreadyExists_ReturnsError() throws Exception {
        final List<Item> items = generateItems();
        final String title = items.get((new Random()).nextInt(items.size())).getTitle().toLowerCase();
        final Item item = Item.builder().category(getRandomCategory()).title(title).priority(Priority.MEDIUM).build();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.saveItem(item, false);
        assertEquals(RandoCubeController.ERROR_TITLE_DUPLICATE + item.getTitle(), response.getError());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).save();
    }

    @Test
    public void saveItem_TitleAlreadyExistsInCompleted_ReturnsError() throws Exception {
        final List<Item> items = generateItems();
        final String title = items.get((new Random()).nextInt(items.size())).getTitle().toLowerCase();
        final Item item = Item.builder().category(getRandomCategory()).title(title).priority(Priority.MEDIUM).build();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final BaseResponse response = itemManager.saveItem(item, false);
        assertEquals(RandoCubeController.ERROR_TITLE_DUPLICATE + item.getTitle(), response.getError());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).save();
    }

    @Test
    public void saveItem_TitleAlreadyExistsFlagTrue_ListUpdated() throws Exception {
        final List<Item> expected = generateItems();
        final String title = expected.get((new Random()).nextInt(expected.size())).getTitle().toLowerCase();
        final Integer id = expected.size() + 1;
        final Item item = Item.builder().category(getRandomCategory()).title(title).priority(Priority.MEDIUM).build();
        expected.add(item);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());
        when(dataHandler.getNextId()).thenReturn(id);

        final BaseResponse response = itemManager.saveItem(item, true);
        item.setId(id);
        item.setAdded(LocalDate.now());
        assertNull(response.getError());
        verify(dataHandler).setData(expected);
        verify(dataHandler).save();
    }

    @Test
    public void saveItem_TitleAlreadyExistsInCompletedFlagTrue_ListUpdated() throws Exception {
        final List<Item> expected = new ArrayList<>();
        final List<Item> mockedItems = generateItems(true);
        final String title = mockedItems.get((new Random()).nextInt(mockedItems.size())).getTitle().toLowerCase();
        final Integer id = mockedItems.size() + 1;
        final Item item = Item.builder().category(getRandomCategory()).title(title).priority(Priority.MEDIUM).build();
        expected.add(item);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getHistory()).thenReturn(mockedItems);
        when(dataHandler.getNextId()).thenReturn(id);

        final BaseResponse response = itemManager.saveItem(item, true);
        item.setId(id);
        item.setAdded(LocalDate.now());
        assertNull(response.getError());
        verify(dataHandler).setData(expected);
        verify(dataHandler).save();
    }

    @Test
    public void saveItem_DataError_ReturnsError() throws Exception {
        final Item item = Item.builder().category(getRandomCategory()).title(testTitle).priority(Priority.MEDIUM).build();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());
        doThrow(new IOException("Test")).when(dataHandler).save();

        final BaseResponse response = itemManager.saveItem(item, false);
        assertEquals("Could not save item: " + item.getTitle() + ", check logs: Test", response.getError());
    }

    /* REMOVE ITEM */
    @Test
    public void removeItem_RemoveExistingItem_ListUpdated() throws Exception {
        final List<Item> expected = generateItems();
        final int id = (new Random()).nextInt(expected.size()) + 1;
        expected.remove(id - 1);

        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.removeItem(id);
        assertNull(response.getError());
        verify(dataHandler).setData(expected);
        verify(dataHandler, never()).setHistory(anyList());
        verify(dataHandler).save();
    }

    @Test
    public void removeItem_RemoveCompletedItem_ListUpdated() throws Exception {
        final List<Item> expected = generateItems(true);
        final int id = (new Random()).nextInt(expected.size()) + 1;
        expected.remove(id - 1);

        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final BaseResponse response = itemManager.removeItem(id);
        assertNull(response.getError());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler).setHistory(expected);
        verify(dataHandler).save();
    }

    @Test
    public void removeItem_IdDoesNotExist_ReturnsError() throws Exception {
        final Integer id = -1;

        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.removeItem(id);
        assertEquals(RandoCubeController.ERROR_ID_NOT_FOUND + id, response.getError());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setHistory(anyList());
        verify(dataHandler, never()).save();
    }

    @Test
    public void removeItem_DataError_ReturnsError() throws Exception {
        final List<Item> items = generateItems();
        final int id = (new Random()).nextInt(items.size()) + 1;
        final String title = items.get(id - 1).getTitle();

        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());
        doThrow(new IOException("Test")).when(dataHandler).save();

        final BaseResponse response = itemManager.removeItem(id);
        assertEquals("Could not remove item: " + title + ", check logs: Test", response.getError());
    }


    /* MARK COMPLETED */
    @Test
    public void markCompleted_MarkExistingItem_ListsUpdated() throws Exception {
        final List<Item> expected = generateItems();
        final int id = (new Random()).nextInt(expected.size()) + 1;
        final Item marked = expected.remove(id - 1);
        marked.setCompleted(LocalDate.now());
        final List<Item> markedItems = List.of(marked);

        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.markCompleted(id, false);
        assertNull(response.getError());
        verify(dataHandler).setData(expected);
        verify(dataHandler).setHistory(markedItems);
        verify(dataHandler).save();
    }

    @Test
    public void markCompleted_UnmarkCompletedItem_ListsUpdated() throws Exception {
        final List<Item> expected = generateItems(true);
        final int id = (new Random()).nextInt(expected.size()) + 1;
        final Item unmarked = expected.remove(id - 1);
        unmarked.setCompleted(null);
        final List<Item> unmarkedItems = List.of(unmarked);

        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final BaseResponse response = itemManager.markCompleted(id, true);
        assertNull(response.getError());
        verify(dataHandler).setData(unmarkedItems);
        verify(dataHandler).setHistory(expected);
        verify(dataHandler).save();
    }

    @Test
    public void markCompleted_UnmarkedIdDoesNotExist_ReturnsError() throws Exception {
        final Integer id = -1;

        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.markCompleted(id, false);
        assertEquals(RandoCubeController.ERROR_ID_NOT_FOUND + id, response.getError());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setHistory(anyList());
        verify(dataHandler, never()).save();
    }

    @Test
    public void markCompleted_MarkedIdDoesNotExist_ReturnsError() throws Exception {
        final Integer id = -1;

        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final BaseResponse response = itemManager.markCompleted(id, true);
        assertEquals(RandoCubeController.ERROR_ID_NOT_FOUND + id, response.getError());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler, never()).setHistory(anyList());
        verify(dataHandler, never()).save();
    }

    @Test
    public void markCompleted_DataError_ReturnsError() throws Exception {
        final List<Item> items = generateItems();
        final int id = (new Random()).nextInt(items.size()) + 1;
        final String title = items.get(id - 1).getTitle();

        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());
        doThrow(new IOException("Test")).when(dataHandler).save();

        final BaseResponse response = itemManager.markCompleted(id, false);
        assertEquals("Could not mark/unmark item: " + title + ", check logs: Test", response.getError());
    }
}
