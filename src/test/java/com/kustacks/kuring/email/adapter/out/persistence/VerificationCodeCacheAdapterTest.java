package com.kustacks.kuring.email.adapter.out.persistence;

import com.kustacks.kuring.email.adapter.out.cache.VerificationCodeCacheAdapter;
import com.kustacks.kuring.support.IntegrationTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

@DisplayName("인증 코드 캐싱 테스트")
class VerificationCodeCacheAdapterTest extends IntegrationTestSupport {

    @Autowired
    private VerificationCodeCacheAdapter verificationCodeCacheAdapter;

    @Autowired
    private CacheManager cacheManager;

    private final String CACHE_NAME = "verificationCodeCache";

    @DisplayName("인증 코드 캐싱 저장 테스트")
    @Test
    public void save_code_cache() throws Exception {
        //given
        String email = "test@test.com";
        String code = "123456789";

        //when, then
        verificationCodeCacheAdapter.saveCode(email, code);

        //then
        String resultCode = (String) cacheManager.getCache(CACHE_NAME).get(email)
                .get();
        Assertions.assertThat(resultCode)
                .isEqualTo(code);
    }

    @DisplayName("인증 코드 캐싱 조회 테스트")
    @Test
    public void search_saved_code() throws Exception {
        //given
        String email = "test@test.com";
        String code = "123456789";

        //when, then
        cacheManager.getCache(CACHE_NAME).put(email, code);

        //then
        String codeByEmail = verificationCodeCacheAdapter.findCodeByEmail(email);
        Assertions.assertThat(codeByEmail)
                .isEqualTo(code);
    }
}