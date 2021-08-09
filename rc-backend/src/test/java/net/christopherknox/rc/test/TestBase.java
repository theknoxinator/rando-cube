package net.christopherknox.rc.test;

import net.christopherknox.rc.model.Item;
import org.assertj.core.util.Lists;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public abstract class TestBase {
    protected static final List<String> exampleCategories = List.of("Books", "Board Games", "Video Games", "Movies/TV");
    protected static final String testCategory = "Test Category";
    protected static final String testTitle = "Test Title";

    protected static Stream<Arguments> generateCategoryArguments() {
        return exampleCategories.stream().map(Arguments::of);
    }

    protected static List<String> generateCategories() {
        return Lists.newArrayList(exampleCategories);
    }

    protected static String getRandomCategory() {
        return exampleCategories.get((new Random()).nextInt(exampleCategories.size()));
    }

    protected static List<Item> generateItems(final String category) {
        List<Item> items = new ArrayList<>();
        if (category != null) {
            for (int i = 1; i <= 3; i++) {
                items.add(Item.builder().id(i).category(category).title(testTitle + " " + i).build());
            }
        } else {
            int i = 1;
            exampleCategories.forEach(c ->
                items.add(Item.builder().id(i).category(c).title(testTitle + " " + i).build()));
        }
        return items;
    }

    protected static Map<String, List<Item>> generateLastSets(final List<Item> items) {
        Map<String, List<Item>> lastSets = new HashMap<>();
        List<Item> itemsToProcess = items;
        if (itemsToProcess == null) {
            itemsToProcess = generateItems(null);
        }
        itemsToProcess.forEach(i -> lastSets.computeIfAbsent(i.getCategory(), k -> new ArrayList<>()).add(i));
        return lastSets;
    }
}
