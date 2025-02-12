package com.kustacks.kuring.email.adapter.out.cache;

import com.kustacks.kuring.common.annotation.PersistenceAdapter;
import com.kustacks.kuring.email.application.port.out.VerificationCodeCommandPort;
import com.kustacks.kuring.email.application.port.out.VerificationCodeQueryPort;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@CacheConfig(cacheNames = "verificationCodeCache")
@PersistenceAdapter
public class VerificationCodeCacheAdapter implements VerificationCodeCommandPort, VerificationCodeQueryPort {

    @Override
    @CachePut(key = "#email")
    public String saveCode(String email, String verificationCode) {
        return verificationCode;
    }

    @Override
    @Cacheable(key = "#email")
    public String findCodeByEmail(String email) {
        return null;
    }
}
