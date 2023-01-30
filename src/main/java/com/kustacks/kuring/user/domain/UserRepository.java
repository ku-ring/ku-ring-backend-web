package com.kustacks.kuring.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByToken(String token);

    @Transactional
    void deleteByToken(String token);
}
