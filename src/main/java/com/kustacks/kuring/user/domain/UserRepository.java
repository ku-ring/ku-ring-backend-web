package com.kustacks.kuring.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {
    Optional<User> findByToken(String token);

    @Query("SELECT u.token FROM User u")
    List<String> findAllToken();
}
