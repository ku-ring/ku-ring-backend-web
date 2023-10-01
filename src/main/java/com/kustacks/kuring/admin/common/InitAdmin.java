package com.kustacks.kuring.admin.common;

import com.kustacks.kuring.admin.domain.Admin;
import com.kustacks.kuring.admin.domain.AdminRepository;
import com.kustacks.kuring.admin.domain.AdminRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitAdmin implements InitializingBean {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        Optional<Admin> optionalAdmin = adminRepository.findByLoginId(adminProperties.getId());

        if(optionalAdmin.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(adminProperties.getPassword());
            Admin admin = new Admin(adminProperties.getId(), encodedPassword);
            admin.addRole(AdminRole.ROLE_ROOT);
            adminRepository.save(admin);
        }
    }
}
