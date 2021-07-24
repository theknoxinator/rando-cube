package net.christopherknox.rc.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.christopherknox.rc.ItemManager;
import net.christopherknox.rc.RandoCubeController;
import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.response.ItemListResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class RandoCubeControllerTests {

    private static final String HEALTH_ENDPOINT = "/health";
    private static final String GET_RANDOM_SET_ENDPOINT = "/getRandomSet";
    private static final String GET_FULL_LIST_ENDPOINT = "/getFullList";
    private static final String GET_COMPLETED_LIST_ENDPOINT = "/getCompletedList";
    private static final String MARK_COMPLETED_ENDPOINT = "/markCompleted";
    private static final String ADD_ITEM_ENDPOINT = "/addItem";
    private static final String REMOVE_ITEM_ENDPOINT = "/removeItem";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemManager itemManager;

    @Test
    public void pingHealth_ServiceIsRunning_ReceiveAliveStatusMessage() throws Exception {
        mockMvc.perform(get(HEALTH_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(RandoCubeController.HEALTH_MESSAGE)));
    }


    /* GET RANDOM SET */
    @ParameterizedTest
    @MethodSource("generateCategoryAndUseLast")
    public void getRandomSet_ValidCategoryAndUseLast_ReturnsValidSet(final String category, final boolean useLast) throws Exception {
        final ItemListResponse response = generateItemListResponse(category, null);

        when(itemManager.getRandomSet(category, useLast)).thenReturn(response);

        mockMvc.perform(get(GET_RANDOM_SET_ENDPOINT).queryParam("category", category)
                .queryParam("useLast", Boolean.toString(useLast)))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @ParameterizedTest
    @MethodSource("generateCategory")
    public void getRandomSet_ValidCategory_ReturnsValidSet(final String category) throws Exception {
        final ItemListResponse response = generateItemListResponse(category, null);

        when(itemManager.getRandomSet(category, true)).thenReturn(response);

        mockMvc.perform(get(GET_RANDOM_SET_ENDPOINT).queryParam("category", category))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void getRandomSet_MissingCategory_ReturnsBadRequestError() throws Exception {
        mockMvc.perform(get(GET_RANDOM_SET_ENDPOINT))
            .andExpect(status().isBadRequest());
    }


    /* GET FULL LIST */
    @ParameterizedTest
    @MethodSource("generateCategory")
    public void getFullList_ValidCategory_ReturnsValidList(final String category) throws Exception {
        final ItemListResponse response = generateItemListResponse(category, null);

        when(itemManager.getFullList(category)).thenReturn(response);

        mockMvc.perform(get(GET_FULL_LIST_ENDPOINT).queryParam("category", category))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void getFullList_MissingCategory_ReturnsValidList() throws Exception {
        final ItemListResponse response = generateItemListResponse(null, null);

        when(itemManager.getFullList(null)).thenReturn(response);

        mockMvc.perform(get(GET_FULL_LIST_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }


    /* GET COMPLETED LIST */
    @ParameterizedTest
    @MethodSource("generateCategory")
    public void getCompletedList_ValidCategory_ReturnsValidList(final String category) throws Exception {
        final ItemListResponse response = generateItemListResponse(category, null);

        when(itemManager.getCompletedList(category)).thenReturn(response);

        mockMvc.perform(get(GET_COMPLETED_LIST_ENDPOINT).queryParam("category", category))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }

    @Test
    public void getCompletedList_MissingCategory_ReturnsValidList() throws Exception {
        final ItemListResponse response = generateItemListResponse(null, null);

        when(itemManager.getCompletedList(null)).thenReturn(response);

        mockMvc.perform(get(GET_COMPLETED_LIST_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(response)));
    }



    /* HELPER FUNCTIONS */
    private static ItemListResponse generateItemListResponse(final String category, final String error) {
        List<Item> items = new ArrayList<>();
        if (category != null) {
            for (int i = 1; i <= 3; i++) {
                items.add(Item.builder().id(i).category(category).title("Test Title " + i).build());
            }
        } else {
            int i = 1;
            generateCategory().forEach(arguments -> {
                items.add(Item.builder().id(i).category((String) arguments.get()[0]).title("Test Title " + i).build());
            });
        }

        return ItemListResponse.builder()
            .items(items)
            .error(error)
            .build();
    }

    private static Stream<Arguments> generateCategory() {
        return Stream.of(
            Arguments.of("Book"),
            Arguments.of("Board Games"),
            Arguments.of("Video Games"),
            Arguments.of("Movies/TV")
        );
    }

    private static Stream<Arguments> generateCategoryAndUseLast() {
        return Stream.of(
            Arguments.of("Book", false),
            Arguments.of("Book", true),
            Arguments.of("Board Games", false),
            Arguments.of("Board Games", true),
            Arguments.of("Video Games", false),
            Arguments.of("Video Games", true),
            Arguments.of("Movies/TV", false),
            Arguments.of("Movies/TV", true)
        );
    }

    private String toJson(final Object response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(response);
        } catch (Exception e) {
            fail("Could not convert to JSON: " + response.toString());
        }
        return "";
    }
}
