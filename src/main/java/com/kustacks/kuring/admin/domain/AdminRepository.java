package com.kustacks.kuring.admin.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByToken(String token);
}
