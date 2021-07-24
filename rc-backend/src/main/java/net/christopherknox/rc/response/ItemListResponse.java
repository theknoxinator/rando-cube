package net.christopherknox.rc.response;

import lombok.Builder;
import lombok.Data;
import net.christopherknox.rc.model.Item;

import java.util.List;

@Data
@Builder
public class ItemListResponse {
    private List<Item> items;
    private String error;
}
