package com.kustacks.kuring.club.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("도메인 : ClubSnsType")
class ClubSnsTypeTest {

    @DisplayName("URL host를 기준으로 SNS 타입을 판별한다")
    @ParameterizedTest
    @CsvSource({
            "https://www.instagram.com/kuring,INSTAGRAM",
            "https://instagram.com/kuring,INSTAGRAM",
            "https://m.instagram.com/kuring,INSTAGRAM",
            "https://www.youtube.com/@kuring,YOUTUBE",
            "https://youtube.com/@kuring,YOUTUBE",
            "https://youtu.be/abc123,YOUTUBE"
    })
    void fromUrl(String url, ClubSnsType expected) {
        ClubSnsType result = ClubSnsType.fromUrl(url);

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("유사 도메인은 매칭하지 않는다")
    @Test
    void fromUrl_withMaliciousDomain() {
        ClubSnsType result = ClubSnsType.fromUrl("https://www.youtube.com.evil.com/attack");

        assertThat(result).isEqualTo(ClubSnsType.ETC);
    }

    @DisplayName("잘못된 URL 또는 빈 값은 ETC로 처리한다")
    @ParameterizedTest
    @CsvSource({
            "'',ETC",
            "'   ',ETC",
            "not-a-url,ETC",
            "https://example.com,ETC"
    })
    void fromUrl_withInvalidOrUnsupported(String url, ClubSnsType expected) {
        ClubSnsType result = ClubSnsType.fromUrl(url);

        assertThat(result).isEqualTo(expected);
    }
}
