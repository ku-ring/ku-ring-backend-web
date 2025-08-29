package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.common.utils.converter.StringToDateTimeConverter;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.kustacks.kuring.calendar.domain.Transparent.OPAQUE;

public class AcademicEventConverter {

    public static List<AcademicEvent> convertToAcademicEvents(List<IcsEvent> icsEvents) {
        return icsEvents.stream()
                .map(AcademicEventConverter::convertToAcademicEvent)
                .filter(Objects::nonNull)
                .toList();
    }

    public static AcademicEvent convertToAcademicEvent(IcsEvent icsEvent) {
        String uid = parseString(icsEvent.uid());
        String summary = parseString(icsEvent.summary());
        String description = parseString(icsEvent.description());

        LocalDateTime startTime = StringToDateTimeConverter.convert(icsEvent.dtstart());
        LocalDateTime endTime = StringToDateTimeConverter.convert(icsEvent.dtend());

        //초기 기타로 통일, 별도 분류 필요 25.08.26 김한주
        String category = "ETC";
        Transparent transparent = convertToTransparent(icsEvent.transp());
        Integer sequence = convertToSequence(icsEvent.sequence());

        boolean notifyEnabled = determineNotifyEnabled(transparent);

        try {
            return AcademicEvent.from(uid, summary, description,
                    category, transparent, sequence, notifyEnabled, startTime, endTime);
        } catch (Exception e) {
            return null;
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
        if (Objects.isNull(sequence)) {
            return 0;
        }

        try {
            return Integer.parseInt(sequence);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean determineNotifyEnabled(Transparent transparent) {
        if (Objects.isNull(transparent)) {
            return false;
        }

        return switch (transparent) {
            case OPAQUE -> true;  // 중요한 일정은 알림 활성화
            case TRANSPARENT -> false; // 덜 중요한 일정은 알림 비활성화
            default -> false;
        };
    }
}