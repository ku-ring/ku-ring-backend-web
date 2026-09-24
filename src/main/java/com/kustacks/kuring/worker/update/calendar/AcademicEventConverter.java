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

        // 3. 종일 일정이 이틀에 걸쳐서 표시되지 않도록 수정
        try {
            if (icsEvent.dtstart() == null || icsEvent.dtstart().isBlank()) {
                log.warn("DTSTART가 존재하지 않습니다. (uid={}, summary={})", uid, summary);
                return Optional.empty();
            }

            LocalDateTime startTime = StringToDateTimeConverter.convert(icsEvent.dtstart());
            LocalDateTime endTime = calculateEndTime(icsEvent, startTime);

            AcademicEventCategory category = AcademicEventCategorizer.categorize(summary);
            Transparent transparent = convertToTransparent(icsEvent.transp());
            Integer sequence = convertToSequence(icsEvent.sequence());
            boolean notifyEnabled = AcademicEventNotificationClassifier.proceed(transparent, summary);

            if (endTime.isBefore(startTime)) {
                log.warn("DTEND가 DTSTART보다 이전입니다. (uid={}, dtstart={}, dtend={})",
                        uid, icsEvent.dtstart(), icsEvent.dtend());
                return Optional.empty();
            }

            return Optional.of(
                    AcademicEvent.from(uid, summary, description, category,
                            transparent, sequence, notifyEnabled, startTime, endTime)
            );
        } catch (Exception e) {
            log.warn("ICS event 변환에 실패했습니다.(uid={}, summary={}): {}", uid, summary, e.toString());
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

    /**
     * 종일 일정의 종료 시간을 해당 날짜의 23시 59분 59초로 설정하는 메서드
     */
    private static LocalDateTime adjustAllDayEndTime(LocalDateTime endTime) {
        return endTime.toLocalDate()
                .atTime(23, 59, 59);
    }

    /**
     * 종료 일자를 계산하는 메서드
     */
    private static LocalDateTime calculateEndTime(IcsEvent icsEvent, LocalDateTime startTime) {
        if (icsEvent.dtend() == null || icsEvent.dtend().isBlank()) {
            if (icsEvent.dtstartAllDay()) {
                return adjustAllDayEndTime(startTime);
            }
            return startTime;
        }

        LocalDateTime endTime = StringToDateTimeConverter.convert(icsEvent.dtend());

        if (icsEvent.dtstartAllDay() && icsEvent.dtendAllDay()) {
            return adjustAllDayEndTime(endTime.minusDays(1));
        }

        return endTime;
    }

}