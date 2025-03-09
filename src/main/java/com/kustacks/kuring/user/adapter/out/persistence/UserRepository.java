package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {
    Optional<User> findByFcmToken(String fcmToken);

    @Query("SELECT u.fcmToken FROM User u")
    List<String> findAllFcmTokens();
}