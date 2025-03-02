package com.kustacks.kuring.user.adapter.out.persistence;

import com.kustacks.kuring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface UserRepository extends JpaRepository<User, Long>, UserQueryRepository {
    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);
}