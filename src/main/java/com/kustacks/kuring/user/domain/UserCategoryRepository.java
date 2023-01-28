package com.kustacks.kuring.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCategoryRepository extends JpaRepository<UserCategory, Long>, UserCategoryQueryRepository {

    void deleteAllByUser(User user);
}
