package com.kustacks.kuring.category.business;

import com.kustacks.kuring.notice.domain.CategoryName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoryService {

    public List<CategoryName> lookUpSupportedCategories() {
        return List.of(CategoryName.values());
    }
}
