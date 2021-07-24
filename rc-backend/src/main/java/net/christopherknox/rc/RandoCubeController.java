package net.christopherknox.rc;

import net.christopherknox.rc.response.ItemListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RandoCubeController {
    public static final String HEALTH_MESSAGE = "RandoCube is up and running!";

    private final ItemManager itemManager;

    public RandoCubeController(final ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @GetMapping("/health")
    public String pingHealth() {
        return HEALTH_MESSAGE;
    }

    @GetMapping("/getRandomSet")
    public ItemListResponse getRandomSet(@RequestParam("category") final String category,
        @RequestParam(name="useLast", required=false, defaultValue="true") final boolean useLast) {
        return itemManager.getRandomSet(category, useLast);
    }

    @GetMapping("/getFullList")
    public ItemListResponse getFullList(@RequestParam(name="category", required=false) final String category) {
        return itemManager.getFullList(category);
    }

    @GetMapping("/getCompletedList")
    public ItemListResponse getCompletedList(@RequestParam(name="category", required=false) final String category) {
        return itemManager.getCompletedList(category);
    }
}
