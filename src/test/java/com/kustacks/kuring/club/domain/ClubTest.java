package com.kustacks.kuring.club.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("도메인 : Club")
class ClubTest {

    @Test
    @DisplayName("homepageUrls는 Club이 ClubSns 생명주기를 관리하도록 설정된다")
    void homepage_urls_should_manage_club_sns_lifecycle() throws NoSuchFieldException {
        Field homepageUrlsField = Club.class.getDeclaredField("homepageUrls");
        OneToMany mapping = homepageUrlsField.getAnnotation(OneToMany.class);

        assertThat(mapping).isNotNull();
        assertThat(mapping.orphanRemoval()).isTrue();
        assertThat(Arrays.asList(mapping.cascade())).contains(CascadeType.ALL);
    }
}
