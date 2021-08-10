package net.christopherknox.rc.test;

import net.christopherknox.rc.DataHandler;
import net.christopherknox.rc.ItemManager;
import net.christopherknox.rc.RandoCubeController;
import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.ItemListResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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


    /* GET FULL LIST */
    @Test
    public void getFullList_GetAllExistingItems_ReturnsFullList() {
        final List<Item> items = generateItems();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());

        final ItemListResponse response = itemManager.getFullList(null);
        assertEquals(items, response.getItems());
        assertNull(response.getError());
    }

    @Test
    public void getFullList_GetAllNoExistingItems_ReturnsEmptyList() {
        final List<Item> items = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(new ArrayList<>());

        final ItemListResponse response = itemManager.getFullList(null);
        assertEquals(items, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getFullList_GetByCategoryExistingItems_ReturnsCategoryList(final String category) {
        final List<Item> items = generateItems(category);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(generateItems());

        final ItemListResponse response = itemManager.getFullList(category);
        assertEquals(items, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getFullList_GetByCategoryNoExistingItems_ReturnsEmptyList(final String category) {
        final List<Item> items = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getData()).thenReturn(new ArrayList<>());

        final ItemListResponse response = itemManager.getFullList(category);
        assertEquals(items, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getFullList_GetByCategoryNoCategoryItems_ReturnsEmptyList(final String category) {
        final List<Item> items = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        final List<Item> mockedItems =
            generateItems().stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        when(dataHandler.getData()).thenReturn(mockedItems);

        final ItemListResponse response = itemManager.getFullList(category);
        assertEquals(items, response.getItems());
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
        final List<Item> items = generateItems(true);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final ItemListResponse response = itemManager.getCompletedList(null);
        assertEquals(items, response.getItems());
        assertNull(response.getError());
    }

    @Test
    public void getCompletedList_GetAllNoExistingItems_ReturnsEmptyList() {
        final List<Item> items = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final ItemListResponse response = itemManager.getCompletedList(null);
        assertEquals(items, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getCompletedList_GetByCategoryExistingItems_ReturnsCategoryList(final String category) {
        final List<Item> items = generateItems(category, true);

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final ItemListResponse response = itemManager.getCompletedList(category);
        assertEquals(items, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getCompletedList_GetByCategoryNoExistingItems_ReturnsEmptyList(final String category) {
        final List<Item> items = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final ItemListResponse response = itemManager.getCompletedList(category);
        assertEquals(items, response.getItems());
        assertNull(response.getError());
    }

    @ParameterizedTest
    @MethodSource("generateCategoryArguments")
    public void getCompletedList_GetByCategoryNoCategoryItems_ReturnsEmptyList(final String category) {
        final List<Item> items = new ArrayList<>();

        when(dataHandler.getCategories()).thenReturn(generateCategories());
        final List<Item> mockedItems =
            generateItems(true).stream().filter(i -> !i.getCategory().equals(category)).collect(Collectors.toList());
        when(dataHandler.getHistory()).thenReturn(mockedItems);

        final ItemListResponse response = itemManager.getCompletedList(category);
        assertEquals(items, response.getItems());
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


    /* REMOVE ITEM */
    @Test
    public void removeItem_RemoveExistingItem_ListUpdated() throws Exception {
        final List<Item> items = generateItems();
        final int id = (new Random()).nextInt(items.size()) + 1;
        items.remove(id - 1);

        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.removeItem(id);
        assertNull(response.getError());
        verify(dataHandler).setData(items);
        verify(dataHandler, never()).setHistory(anyList());
        verify(dataHandler).save();
    }

    @Test
    public void removeItem_RemoveCompletedItem_ListUpdated() throws Exception {
        final List<Item> items = generateItems(true);
        final int id = (new Random()).nextInt(items.size()) + 1;
        items.remove(id - 1);

        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final BaseResponse response = itemManager.removeItem(id);
        assertNull(response.getError());
        verify(dataHandler, never()).setData(anyList());
        verify(dataHandler).setHistory(items);
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
        final List<Item> items = generateItems();
        final int id = (new Random()).nextInt(items.size()) + 1;
        final Item marked = items.remove(id - 1);
        marked.setCompleted(LocalDate.now());
        final List<Item> markedItems = List.of(marked);

        when(dataHandler.getData()).thenReturn(generateItems());
        when(dataHandler.getHistory()).thenReturn(new ArrayList<>());

        final BaseResponse response = itemManager.markCompleted(id, false);
        assertNull(response.getError());
        verify(dataHandler).setData(items);
        verify(dataHandler).setHistory(markedItems);
        verify(dataHandler).save();
    }

    @Test
    public void markCompleted_UnmarkCompletedItem_ListsUpdated() throws Exception {
        final List<Item> items = generateItems(true);
        final int id = (new Random()).nextInt(items.size()) + 1;
        final Item unmarked = items.remove(id - 1);
        unmarked.setCompleted(null);
        final List<Item> unmarkedItems = List.of(unmarked);

        when(dataHandler.getData()).thenReturn(new ArrayList<>());
        when(dataHandler.getHistory()).thenReturn(generateItems(true));

        final BaseResponse response = itemManager.markCompleted(id, true);
        assertNull(response.getError());
        verify(dataHandler).setData(unmarkedItems);
        verify(dataHandler).setHistory(items);
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
