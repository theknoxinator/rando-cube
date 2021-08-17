package net.christopherknox.rc.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.christopherknox.rc.DataHandler;
import net.christopherknox.rc.model.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource("/test.properties")
public class DataHandlerTests extends TestBase {

    @Autowired
    private DataHandler dataHandler;

    @Value("${data.filepath}")
    private String testFilepath;

    @BeforeAll
    public static void deleteTestFiles() throws IOException {
        DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get("src/test/resources"), "*.json");
        for (Path path : paths) {
            System.out.println("Deleting: " + path);
            Files.delete(path);
        }
    }

    @Test
    public void init_FileDoesNotExist_DefaultsLoaded() throws Exception {
        deleteTestFiles();

        dataHandler.init();

        assertEquals(new ArrayList<>(), dataHandler.getData());
        assertEquals(new ArrayList<>(), dataHandler.getHistory());
        assertEquals(new ArrayList<>(), dataHandler.getCategories());
        assertEquals(new HashMap<>(), dataHandler.getLastSets());
        assertEquals(3, dataHandler.getDefaultSetSize());
        assertEquals(1, dataHandler.getNextId());
    }

    @Test
    public void init_FileDoesExist_FileLoaded() throws Exception {
        DataHandler.Data testData = generateTestData();
        saveTestData(testData);

        dataHandler.init();

        assertEquals(testData.getData(), dataHandler.getData());
        assertEquals(testData.getHistory(), dataHandler.getHistory());
        assertEquals(testData.getCategories(), dataHandler.getCategories());
        assertEquals(testData.getLastSets(), dataHandler.getLastSets());
        assertEquals(testData.getDefaultSetSize(), dataHandler.getDefaultSetSize());
        assertEquals(testData.getNextId(), dataHandler.getNextId());
    }

    @Test
    public void reload_FileDoesNotExist_ThrowsException() throws Exception {
        deleteTestFiles();

        assertThrows(FileNotFoundException.class, () -> dataHandler.reload());
    }

    @Test
    public void reload_FileDoesExist_FileLoaded() throws Exception {
        DataHandler.Data testData = generateTestData();
        saveTestData(testData);

        dataHandler.reload();

        assertEquals(testData.getData(), dataHandler.getData());
        assertEquals(testData.getHistory(), dataHandler.getHistory());
        assertEquals(testData.getCategories(), dataHandler.getCategories());
        assertEquals(testData.getLastSets(), dataHandler.getLastSets());
        assertEquals(testData.getDefaultSetSize(), dataHandler.getDefaultSetSize());
        assertEquals(testData.getNextId(), dataHandler.getNextId());
    }

    @Test
    public void save_FileDoesNotExist_FileCreatedWithData() throws Exception {
        deleteTestFiles();
        final List<Item> items = generateItems(5);
        final List<Item> completed = generateItems(testCategory, true, 2);
        final List<String> categories = generateCategories();
        final Map<String, List<Item>> lastSets = generateLastSets(items);
        final Integer defaultSetSize = 5;
        final Integer nextId = dataHandler.getNextId() + 1;

        dataHandler.setData(items);
        dataHandler.setHistory(completed);
        dataHandler.setCategories(categories);
        dataHandler.setLastSets(lastSets);
        dataHandler.setDefaultSetSize(defaultSetSize);
        dataHandler.save();

        assertTrue(Files.exists(Paths.get(testFilepath)));
        DataHandler.Data testData = getTestData();
        assertEquals(items, testData.getData());
        assertEquals(completed, testData.getHistory());
        assertEquals(categories, testData.getCategories());
        assertEquals(lastSets, testData.getLastSets());
        assertEquals(defaultSetSize, testData.getDefaultSetSize());
        assertEquals(nextId, testData.getNextId());
    }

    @Test
    public void save_FileDoesExist_FileOverwrittenWithData() throws Exception {
        saveTestData(generateTestData());
        final List<Item> items = generateItems(5);
        final List<Item> completed = generateItems(testCategory, true, 2);
        final List<String> categories = generateCategories();
        final Map<String, List<Item>> lastSets = generateLastSets(items);
        final Integer defaultSetSize = 5;
        final Integer nextId = dataHandler.getNextId() + 1;

        dataHandler.setData(items);
        dataHandler.setHistory(completed);
        dataHandler.setCategories(categories);
        dataHandler.setLastSets(lastSets);
        dataHandler.setDefaultSetSize(defaultSetSize);
        dataHandler.save();

        assertTrue(Files.exists(Paths.get(testFilepath)));
        DataHandler.Data testData = getTestData();
        assertEquals(items, testData.getData());
        assertEquals(completed, testData.getHistory());
        assertEquals(categories, testData.getCategories());
        assertEquals(lastSets, testData.getLastSets());
        assertEquals(defaultSetSize, testData.getDefaultSetSize());
        assertEquals(nextId, testData.getNextId());
    }

    @Test
    public void nextId_CalledMultipleTimes_IdIncrementsOnEachCall() {
        Integer expected = dataHandler.getNextId();
        for (int i = 1; i <= 3; i++) {
            Integer nextId = dataHandler.getNextId();
            assertEquals(expected + i, nextId);
        }
    }

    private DataHandler.Data generateTestData() {
        Random rand = new Random();
        DataHandler.Data testData = new DataHandler.Data();
        testData.setData(generateItems(rand.nextInt(10)));
        testData.setHistory(new ArrayList<>());
        testData.setCategories(generateCategories());
        testData.setLastSets(generateLastSets(testData.getData()));
        testData.setDefaultSetSize(rand.nextInt(4) + 2);
        testData.setNextId(rand.nextInt(1000) + 1);
        return testData;
    }

    private void saveTestData(final DataHandler.Data testData) throws IOException {
        File testfile = new File(testFilepath);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(testfile, testData);
    }

    private DataHandler.Data getTestData() throws IOException {
        File testfile = new File(testFilepath);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(testfile, DataHandler.Data.class);
    }
}
