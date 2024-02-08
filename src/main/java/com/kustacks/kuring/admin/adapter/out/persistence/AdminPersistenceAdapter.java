package com.kustacks.kuring.admin.adapter.out.persistence;

import com.kustacks.kuring.admin.application.port.out.AdminCommandPort;
import com.kustacks.kuring.admin.domain.Admin;
import com.kustacks.kuring.admin.application.port.out.AdminQueryPort;
import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class AdminPersistenceAdapter implements AdminQueryPort, AdminCommandPort {

    private final AdminRepository adminRepository;

    @Override
    public Optional<Admin> findByLoginId(String loginId) {
        return adminRepository.findByLoginId(loginId);
    }

    @Override
    public void save(Admin admin) {
        this.adminRepository.save(admin);
    }
}
