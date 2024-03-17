package com.kustacks.kuring.admin.application.service;

import com.kustacks.kuring.admin.application.port.out.AdminCommandPort;
import com.kustacks.kuring.admin.application.port.out.AdminQueryPort;
import com.kustacks.kuring.admin.domain.Admin;
import com.kustacks.kuring.admin.domain.AdminRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InitAdmin implements InitializingBean {

    private final AdminQueryPort adminQueryPort;
    private final AdminCommandPort adminCommandPort;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Override
    public void afterPropertiesSet() {
        Optional<Admin> optionalAdmin = adminQueryPort.findByLoginId(adminProperties.id());

        if(optionalAdmin.isEmpty()) {
            String encodedPassword = passwordEncoder.encode(adminProperties.password());
            Admin admin = new Admin(adminProperties.id(), encodedPassword);
            admin.addRole(AdminRole.ROLE_ROOT);
            adminCommandPort.save(admin);
        }
    }
}
