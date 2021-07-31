package net.christopherknox.rc;

import net.christopherknox.rc.response.BaseResponse;
import net.christopherknox.rc.response.CategoryListResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CategoryManager {

    public CategoryListResponse getCategories() {
        return CategoryListResponse.builder()
            .categories(new ArrayList<>())
            .build();
    }

    public BaseResponse addCategory(final String category) {
        return BaseResponse.builder().build();
    }

    public BaseResponse editCategory(final String oldCategory, final String newCategory) {
        return BaseResponse.builder().build();
    }

    public BaseResponse removeCategory(final String category, final String migrateTo) {
        return BaseResponse.builder().build();
    }
}
