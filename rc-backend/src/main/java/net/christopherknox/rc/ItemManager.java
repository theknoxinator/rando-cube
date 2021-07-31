package net.christopherknox.rc;

import net.christopherknox.rc.model.Item;
import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.ItemListResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ItemManager {

    public ItemListResponse getRandomSet(final String category, final boolean useLast) {
        return ItemListResponse.builder()
            .items(new ArrayList<>())
            .build();
    }

    public ItemListResponse getFullList(final String category) {
        return ItemListResponse.builder()
            .items(new ArrayList<>())
            .build();
    }

    public ItemListResponse getCompletedList(final String category) {
        return ItemListResponse.builder()
            .items(new ArrayList<>())
            .build();
    }

    public BaseResponse saveItem(final Item item) {
        return BaseResponse.builder().build();
    }

    public BaseResponse removeItem(final Integer id) {
        return BaseResponse.builder().build();
    }

    public BaseResponse markCompleted(final Integer id) {
        return BaseResponse.builder().build();
    }
}
