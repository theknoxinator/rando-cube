package net.christopherknox.rc;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.christopherknox.rc.model.Item;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
@Slf4j
public class DataHandler {
    private List<Item> data;
    private List<Item> history;
    private List<String> categories;
    private Map<String, List<Item>> lastSets;
    private Integer defaultSetSize;
    private Integer nextId;

    public void save() throws IOException {
        File file = new File("test.txt");
        log.info("File location: " + file.getAbsolutePath());
    }

    public void reload() throws IOException {

    }
}
