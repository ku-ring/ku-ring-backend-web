package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.application.port.out.AcademicEventCommandPort;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsCalendarResult;
import com.kustacks.kuring.worker.update.calendar.dto.EventUpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AcademicEventDbSynchronizer {

    private final AcademicEventCommandPort academicEventCommandPort;
    private final AcademicEventQueryPort academicEventQueryPort;

    @Transactional
    public void compareAndUpdateDb(IcsCalendarResult result) {
        log.info("학사일정 DB 동기화 시작. 총 {} 개 이벤트", result.events().size());

        // 1. 모든 이벤트 엔티티 변환
        List<AcademicEvent> newEvents = AcademicEventConverter.convertToAcademicEvents(result.events());

        // 2. 겹치는 이벤트 조회
        List<String> newEventUids = collectEventUids(newEvents);
        Map<String, AcademicEvent> existingEvents = academicEventQueryPort.findEventsInEventUidsAsMap(newEventUids);

        // 3. 신규, 업데이트 목록 분류 및 도메인 로직 적용
        EventUpdateResult eventUpdateResult = classifyEvents(newEvents, existingEvents);

        // 4. 저장, 업데이트
        saveAndUpdate(eventUpdateResult);

        log.debug("신규 이벤트 {} 개, 업데이트 이벤트 {} 개", eventUpdateResult.newEvents().size(), eventUpdateResult.updatedEvents().size());
    }

    private void saveAndUpdate(EventUpdateResult eventUpdateResult) {
        if (!eventUpdateResult.newEvents().isEmpty()) {
            academicEventCommandPort.saveAll(eventUpdateResult.newEvents());
        }

        if (!eventUpdateResult.updatedEvents().isEmpty()) {
            academicEventCommandPort.updateAll(eventUpdateResult.updatedEvents());
        }
    }

    private List<String> collectEventUids(List<AcademicEvent> validEvents) {
        return validEvents.stream()
                .map(AcademicEvent::getEventUid)
                .toList();
    }

    private EventUpdateResult classifyEvents(List<AcademicEvent> newEvents, Map<String, AcademicEvent> existingEvents) {
        List<AcademicEvent> eventsToSave = new ArrayList<>();
        List<AcademicEvent> eventsToUpdate = new ArrayList<>();

        for (AcademicEvent newEvent : newEvents) {
            AcademicEvent existingEvent = existingEvents.get(newEvent.getEventUid());

            if (Objects.isNull(existingEvent)) {
                //신규 이벤트
                eventsToSave.add(newEvent);
            } else if (shouldEventUpdate(newEvent, existingEvent)) {
                //업데이트해야할 이벤트
                existingEvent.update(newEvent);
                eventsToUpdate.add(existingEvent);
            }
            //sequence가 같거나 낮은 경우는 무시
        }

        return new EventUpdateResult(eventsToSave, eventsToUpdate);
    }

    private boolean shouldEventUpdate(AcademicEvent newEvent, AcademicEvent existingEvent) {
        return newEvent.getSequence() > existingEvent.getSequence()
                && Objects.equals(newEvent.getEventUid(), existingEvent.getEventUid());
    }
}
