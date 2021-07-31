package net.christopherknox.rc.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditCategoryRequest {
    private String oldCategory;
    private String newCategory;
}
