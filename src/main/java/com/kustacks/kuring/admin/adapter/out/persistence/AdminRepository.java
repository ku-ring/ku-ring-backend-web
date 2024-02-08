package com.kustacks.kuring.admin.adapter.out.persistence;

import com.kustacks.kuring.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByLoginId(String loginId);
}
