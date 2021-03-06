package net.christopherknox.rc.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.christopherknox.rc.model.Item;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemListResponse {
    private List<Item> items;
    private String error;
}
