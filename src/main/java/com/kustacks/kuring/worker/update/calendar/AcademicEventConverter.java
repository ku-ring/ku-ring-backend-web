package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.calendar.domain.AcademicEventCategory;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.common.utils.converter.StringToDateTimeConverter;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.kustacks.kuring.calendar.domain.Transparent.OPAQUE;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AcademicEventConverter {

    // 공휴일 관련 키워드 패턴 (ReDoS 방지)
    private static final Pattern HOLIDAY_PATTERN = Pattern.compile("[^\\r\\n]*공휴일[^\\r\\n]*");

    public static List<AcademicEvent> convertToAcademicEvents(List<IcsEvent> icsEvents) {
        return icsEvents.stream()
                .map(AcademicEventConverter::convertToAcademicEvent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public static Optional<AcademicEvent> convertToAcademicEvent(IcsEvent icsEvent) {
        String uid = parseString(icsEvent.uid());
        String rawSummary = parseString(icsEvent.summary());
        String description = parseString(icsEvent.description());

        // 1. 학사일정 변환 가능 여부 확인 (공휴일 제외)
        if (!shouldConvertToAcademicEvent(rawSummary)) {
            return Optional.empty();
        }

        // 2. summary 전처리 (괄호 안 날짜/시간 제거 등)
        String summary = AcademicEventSummaryNormalizer.normalize(rawSummary);
        LocalDateTime startTime = StringToDateTimeConverter.convert(icsEvent.dtstart());
        LocalDateTime endTime = StringToDateTimeConverter.convert(icsEvent.dtend());

        AcademicEventCategory category = AcademicEventCategorizer.categorize(summary);
        Transparent transparent = convertToTransparent(icsEvent.transp());
        Integer sequence = convertToSequence(icsEvent.sequence());

        boolean notifyEnabled = AcademicEventNotificationClassifier.proceed(transparent, summary);

        try {
            return Optional.of(
                    AcademicEvent.from(uid, summary, description, category,
                            transparent, sequence, notifyEnabled, startTime, endTime)
            );
        } catch (Exception e) {
            log.warn("ICS event 변좐에 실패했습니다.(uid={}, summary={}): {}", uid, summary, e.toString());
            return Optional.empty();
        }
    }

    private static String parseString(String string) {
        if (string == null || string.isBlank()) {
            return null;
        }
        return string.trim();
    }


    private static Transparent convertToTransparent(String transp) {
        if (Objects.isNull(transp)) {
            return OPAQUE; // 기본값: 바쁨
        }
        return Transparent.valueOfString(transp);
    }

    private static Integer convertToSequence(String sequence) {
        if (sequence == null || sequence.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(sequence);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * summary가 학사일정으로 변환 가능한지 필터링
     */
    private static boolean shouldConvertToAcademicEvent(String summary) {
        if (summary == null || summary.isBlank()) {
            return false;
        }

        // 공휴일 관련 이벤트는 제외
        return !isHolidayEvent(summary);
    }

    private static boolean isHolidayEvent(String summary) {
        return HOLIDAY_PATTERN.matcher(summary).matches();
    }

}