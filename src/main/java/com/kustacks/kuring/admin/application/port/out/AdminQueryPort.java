package com.kustacks.kuring.admin.application.port.out;

import com.kustacks.kuring.admin.domain.Admin;

import java.util.Optional;

public interface AdminQueryPort {
    Optional<Admin> findByLoginId(String loginId);
}
