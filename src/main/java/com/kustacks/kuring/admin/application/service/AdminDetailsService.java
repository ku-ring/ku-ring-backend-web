package com.kustacks.kuring.admin.application.service;

import com.kustacks.kuring.admin.application.port.out.AdminQueryPort;
import com.kustacks.kuring.auth.userdetails.UserDetails;
import com.kustacks.kuring.auth.userdetails.UserDetailsServicePort;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsServicePort {

    private final AdminQueryPort adminQueryPort;

    @Override
    public UserDetails loadUserByUsername(String loginId) {
        return adminQueryPort.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
