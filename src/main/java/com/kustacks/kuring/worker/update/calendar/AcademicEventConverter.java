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

            boolean isAllDayEvent = isAllDayEvent(icsEvent.dtstart()); // 종일 일정을 판단하기 위한 boolean 변수

            if (icsEvent.dtend() != null
                    && !icsEvent.dtend().isBlank()
                    && isAllDayEvent != isAllDayEvent(icsEvent.dtend())) {

                log.warn(
                        "DTSTART와 DTEND의 형식이 다릅니다. (uid={}, dtstart={}, dtend={})",
                        uid,
                        icsEvent.dtstart(),
                        icsEvent.dtend()
                );
                return Optional.empty();
            }

            LocalDateTime startTime = StringToDateTimeConverter.convert(icsEvent.dtstart());
            LocalDateTime endTime;

            if (icsEvent.dtend() == null || icsEvent.dtend().isBlank()) { // DTEND가 존재하지 않을 경우
                if (isAllDayEvent) {
                    endTime = startTime.plusDays(1).minusSeconds(1);
                } else {
                    endTime = startTime;
                }
            } else { // DTEND가 정상적으로 존재할 경우
                endTime = StringToDateTimeConverter.convert(icsEvent.dtend());

                if (isAllDayEvent) {
                    endTime = adjustAllDayEndTime(endTime);
                }
            }

            if (endTime.isBefore(startTime)) {
                log.warn("DTEND가 DTSTART보다 이전입니다. (uid={}, dtstart={}, dtend={})",
                        uid, icsEvent.dtstart(), icsEvent.dtend());
                    return Optional.empty();
            }

            AcademicEventCategory category = AcademicEventCategorizer.categorize(summary);
            Transparent transparent = convertToTransparent(icsEvent.transp());
            Integer sequence = convertToSequence(icsEvent.sequence());

            boolean notifyEnabled =
                    AcademicEventNotificationClassifier.proceed(transparent, summary);

            return Optional.of(
                    AcademicEvent.from(uid, summary, description, category,
                            transparent, sequence, notifyEnabled, startTime, endTime
                    )
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
     * 종일 일정인지 판별하는 메서드
     */
    private static boolean isAllDayEvent(String dateTime) {
        return dateTime != null && dateTime.matches("^\\d{8}$");
    }

    /**
     * 종일 일정일 때 endTime의 날짜를 1초 당기는 메서드
     */
    private static LocalDateTime adjustAllDayEndTime(LocalDateTime endTime) {
        return endTime.minusSeconds(1);
    }

}