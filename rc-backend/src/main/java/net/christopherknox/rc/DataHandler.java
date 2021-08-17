package net.christopherknox.rc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.christopherknox.rc.model.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DataHandler {

    @Value("${data.filepath}")
    private String filepath;

    private Data filedata;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            reload();
        } catch (Exception e) {
            log.warn("Could not open file: " + filepath, e);
            filedata = new Data();
        }
    }

    public void save() throws IOException {
        File file = new File(filepath);
        log.info("Saving to: " + file.getAbsolutePath());
        mapper.writeValue(file, filedata);
    }

    public void reload() throws IOException {
        File file = new File(filepath);
        log.info("Reading from: " + file.getAbsolutePath());
        filedata = mapper.readValue(file, Data.class);
    }

    @Getter
    @Setter
    public static class Data {
        private List<Item> data;
        private List<Item> history;
        private List<String> categories;
        private Map<String, List<Item>> lastSets;
        private Integer defaultSetSize;
        private Integer nextId;

        public Data() {
            data = new ArrayList<>();
            history = new ArrayList<>();
            categories = new ArrayList<>();
            lastSets = new HashMap<>();
            defaultSetSize = 3;
            nextId = 1;
        }
    }

    public List<Item> getData() {
        return filedata.getData();
    }

    public void setData(final List<Item> data) {
        filedata.setData(data);
    }

    public List<Item> getHistory() {
        return filedata.getHistory();
    }

    public void setHistory(final List<Item> history) {
        filedata.setHistory(history);
    }

    public List<String> getCategories() {
        return filedata.getCategories();
    }

    public void setCategories(final List<String> categories) {
        filedata.setCategories(categories);
    }

    public Map<String, List<Item>> getLastSets() {
        return filedata.getLastSets();
    }

    public void setLastSets(final Map<String, List<Item>> lastSets) {
        filedata.setLastSets(lastSets);
    }

    public Integer getDefaultSetSize() {
        return filedata.getDefaultSetSize();
    }

    public void setDefaultSetSize(final Integer defaultSetSize) {
        filedata.setDefaultSetSize(defaultSetSize);
    }

    public Integer getNextId() {
        final Integer nextId = filedata.getNextId();
        filedata.setNextId(nextId + 1);
        return nextId;
    }
}
