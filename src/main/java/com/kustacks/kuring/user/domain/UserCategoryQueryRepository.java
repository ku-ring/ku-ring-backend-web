package com.kustacks.kuring.user.domain;

import com.kustacks.kuring.category.domain.CategoryName;

import java.util.List;

public interface UserCategoryQueryRepository {

    List<CategoryName> getUserCategoryNamesByToken(String token);

    List<UserCategory> getUserCategoriesByToken(String token);
}
