package com.kustacks.kuring.category.domain;

import java.util.List;

public interface CategoryQueryRepository {
    List<String> getSupportedCategoryNames();
}
