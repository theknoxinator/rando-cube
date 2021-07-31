package net.christopherknox.rc.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.christopherknox.rc.model.Item;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaveItemRequest {
    private Item item;
}
