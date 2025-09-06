package com.kustacks.kuring.calendar.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("도메인 : AcademicEvent")
class AcademicEventTest {

    private String uid = UUID.randomUUID().toString();
    private String summary = "개강";
    private String description = "2025년 1학기 개강";
    private String category = "ETC";
    private Transparent transparent = Transparent.TRANSPARENT;
    private Integer sequence = 1;
    private Boolean notifyEnabled = true;
    private LocalDateTime startTime = LocalDateTime.of(2025, 3, 1, 9, 0);
    private LocalDateTime endTime = LocalDateTime.of(2025, 3, 1, 18, 0);

    @BeforeEach
    void setUp() {
        uid = UUID.randomUUID().toString();
    }

    @DisplayName("AcademicEvent 생성 성공")
    @Test
    void create_academic_event_success() {// when & then
        assertThatCode(this::createAcademicEvent)
                .doesNotThrowAnyException();
    }

    @DisplayName("AcademicEvent 필드 값 검증")
    @Test
    void validate_academic_event_fields() {
        // when
        AcademicEvent event = createAcademicEvent();

        // then
        assertAll(
                () -> assertThat(event.getEventUid()).isEqualTo(uid),
                () -> assertThat(event.getSummary()).isEqualTo(summary),
                () -> assertThat(event.getDescription()).isEqualTo(description),
                () -> assertThat(event.getCategory()).isEqualTo(category),
                () -> assertThat(event.getTransparent()).isEqualTo(transparent),
                () -> assertThat(event.getSequence()).isEqualTo(sequence),
                () -> assertThat(event.getNotifyEnabled()).isTrue(),
                () -> assertThat(event.getStartTime()).isEqualTo(startTime),
                () -> assertThat(event.getEndTime()).isEqualTo(endTime)
        );
    }

    @DisplayName("도메인 업데이트 메서드 테스트")
    @Test
    void domain_update_method_test() {
        //given
        AcademicEvent existingEvent = AcademicEvent.builder()
                .eventUid("test-uid")
                .summary("기존 제목")
                .description("기존 설명")
                .category("기존 카테고리")
                .transparent(Transparent.OPAQUE)
                .sequence(0)
                .notifyEnabled(true)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        AcademicEvent newEvent = AcademicEvent.builder()
                .eventUid("test-uid")
                .summary("새로운 제목")
                .description("새로운 설명")
                .category("새로운 카테고리")
                .transparent(Transparent.TRANSPARENT)
                .sequence(1)
                .notifyEnabled(false)
                .startTime(startTime.plusHours(1))
                .endTime(endTime.plusHours(1))
                .build();

        // when
        existingEvent.update(newEvent);

        // then
        assertThat(existingEvent.getSummary()).isEqualTo("새로운 제목");
        assertThat(existingEvent.getDescription()).isEqualTo("새로운 설명");
        assertThat(existingEvent.getCategory()).isEqualTo("새로운 카테고리");
        assertThat(existingEvent.getTransparent()).isEqualTo(Transparent.TRANSPARENT);
        assertThat(existingEvent.getSequence()).isEqualTo(1);
        assertThat(existingEvent.getNotifyEnabled()).isFalse();
        assertThat(existingEvent.getStartTime()).isEqualTo(startTime.plusHours(1));
        assertThat(existingEvent.getEndTime()).isEqualTo(endTime.plusHours(1));
    }


    private AcademicEvent createAcademicEvent() {
        return AcademicEvent.builder()
                .eventUid(uid)
                .summary(summary)
                .description(description)
                .category(category)
                .transparent(transparent)
                .sequence(sequence)
                .notifyEnabled(notifyEnabled)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}