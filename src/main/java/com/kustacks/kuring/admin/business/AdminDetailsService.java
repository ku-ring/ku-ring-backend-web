package com.kustacks.kuring.admin.business;

import com.kustacks.kuring.admin.domain.AdminRepository;
import com.kustacks.kuring.auth.userdetails.UserDetails;
import com.kustacks.kuring.auth.userdetails.UserDetailsService;
import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) {
        return adminRepository.findByLoginId(loginId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
