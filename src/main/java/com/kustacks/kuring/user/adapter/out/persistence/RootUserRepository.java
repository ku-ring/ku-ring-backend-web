package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.user.domain.RootUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface RootUserRepository extends JpaRepository<RootUser, Long>, RootUserQueryRepository {
    Optional<RootUser> findByEmail(String email);
    Optional<RootUser> findByNickname(String nickname);

    @Query(value = "SELECT * FROM root_user WHERE email = :email AND deleted = true", nativeQuery = true)
    Optional<RootUser> findDeletedRootUserByEmail(@Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RootUser ru SET ru.questionCount = :monthlyQuestionCount")
    void resetAllRootUserQuestionCount(@Param("monthlyQuestionCount") int monthlyQuestionCount);
}
