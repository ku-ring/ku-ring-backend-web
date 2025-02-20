package com.kustacks.kuring.email.adapter.out.persistence;

import com.kustacks.kuring.email.domain.VerificationCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("인증 코드 영속성 테스트")
class VerificationCodePersistenceAdapterTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @DisplayName("인증 코드 저장 테스트")
    @Test
    void save_verification_code() {
        //given
        VerificationCodePersistenceAdapter adapter = new VerificationCodePersistenceAdapter(verificationCodeRepository);
        VerificationCode verificationCode = new VerificationCode("client@konkuk.ac.kr", "123456");

        //when
        adapter.saveVerificationCode(verificationCode);

        //then
        verify(verificationCodeRepository, Mockito.times(1)).save(verificationCode);
    }

    @DisplayName("인증 코드 조회 테스트")
    @Test
    void search_saved_code() {
        //given
        VerificationCodePersistenceAdapter adapter = new VerificationCodePersistenceAdapter(verificationCodeRepository);
        VerificationCode verificationCode = new VerificationCode("client@konkuk.ac.kr", "123456");
        Mockito.when(verificationCodeRepository.findByEmail("client@konkuk.ac.kr"))
                .thenReturn(List.of(verificationCode));

        //when
        VerificationCode findCode = adapter.findCodesByEmail("client@konkuk.ac.kr").get(0);

        //then
        assertAll(
                () -> assertThat(verificationCode.getCode())
                        .isEqualTo(findCode.getCode()),
                () -> assertThat(verificationCode.getEmail())
                        .isEqualTo(findCode.getEmail()),
                () -> verify(verificationCodeRepository, Mockito.times(1))
                        .findByEmail("client@konkuk.ac.kr")
        );
    }
}