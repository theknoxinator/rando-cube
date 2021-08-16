package net.christopherknox.rc.test;

import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.model.Priority;
import org.assertj.core.util.Lists;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
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

    protected static List<Item> generateItems() {
        return generateItems(false);
    }

    protected static List<Item> generateItems(final String category) {
        return generateItems(category, false);
    }

    protected static List<Item> generateItems(final boolean completed) {
        return generateItems(null, completed);
    }

    protected static List<Item> generateItems(final int categorySize) {
        return generateItems(null, categorySize);
    }

    protected static List<Item> generateItems(final String category, final boolean completed) {
        return generateItems(category, completed, 3);
    }

    protected static List<Item> generateItems(final String category, final int categorySize) {
        return generateItems(category, false, categorySize);
    }

    protected static List<Item> generateItems(final String category, final boolean completed, final int categorySize) {
        List<Item> items = new ArrayList<>();
        int count = 0;
        for (String c : exampleCategories) {
            for (int i = 1; i <= categorySize; i++) {
                items.add(Item.builder()
                    .id(i + (categorySize * count))
                    .category(c)
                    .title(c + " " + testTitle + " " + i)
                    .priority(Priority.MEDIUM)
                    .added(LocalDate.now())
                    .completed(completed ? LocalDate.now() : null)
                    .build());
            }
            count++;
        }
        if (category != null) {
            items = items.stream().filter(i -> i.getCategory().equals(category)).collect(Collectors.toList());
        }
        return items;
    }

    protected static Map<String, List<Item>> generateLastSets() {
        return generateLastSets(null);
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
