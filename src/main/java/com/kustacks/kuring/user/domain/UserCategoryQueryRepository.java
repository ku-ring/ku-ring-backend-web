package com.kustacks.kuring.user.domain;

import java.util.List;

public interface UserCategoryQueryRepository {

    List<String> getUserCategoryNamesByToken(String token);

    List<UserCategory> getUserCategoriesByToken(String token);
}
