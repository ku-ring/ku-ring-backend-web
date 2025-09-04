package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.application.port.out.AcademicEventCommandPort;
import com.kustacks.kuring.calendar.application.port.out.AcademicEventQueryPort;
import com.kustacks.kuring.calendar.domain.AcademicEvent;
import com.kustacks.kuring.calendar.domain.Transparent;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsCalendarProperties;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsCalendarResult;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AcademicEventDbSynchronizer 단위 테스트")
class AcademicEventDbSynchronizerTest {

    @Mock
    private AcademicEventCommandPort academicEventCommandPort;

    @Mock
    private AcademicEventQueryPort academicEventQueryPort;

    @InjectMocks
    private AcademicEventDbSynchronizer academicEventDbSynchronizer;

    private IcsCalendarResult testCalendarResult;
    private List<IcsEvent> testEvents;
    private Map<String, AcademicEvent> existingEventsMap;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        IcsEvent event1 = IcsEvent.builder()
                .uid("UID001")
                .summary("테스트 이벤트 1")
                .dtstart("20240901T090000")
                .dtend("20240901T180000")
                .transp("OPAQUE")
                .sequence("0")
                .description("테스트 이벤트 1 설명")
                .build();

        IcsEvent event2 = IcsEvent.builder()
                .uid("UID002")
                .summary("테스트 이벤트 2")
                .dtstart("20240901T090000")
                .dtend("20240901T180000")
                .transp("OPAQUE")
                .sequence("1")
                .description("테스트 이벤트 2 설명")
                .build();

        testEvents = List.of(event1, event2);
        testCalendarResult = new IcsCalendarResult(
                new IcsCalendarProperties(null, null, null, null),
                null,
                testEvents
        );

        // 기존 데이터 맵 설정 (sequence가 낮은 기존 이벤트)
        AcademicEvent existingEvent = AcademicEvent.builder()
                .eventUid("UID002")
                .summary("기존 테스트 이벤트 2")
                .startTime(LocalDateTime.of(2024, 3, 2, 9, 0))
                .endTime(LocalDateTime.of(2024, 3, 2, 17, 0))
                .category("기본")
                .transparent(Transparent.TRANSPARENT)
                .sequence(0) // 새 이벤트보다 sequence가 낮음
                .notifyEnabled(true)
                .build();
        setId(existingEvent, 1L);

        existingEventsMap = new HashMap<>();
        existingEventsMap.put("UID002", existingEvent);
    }

    private void setId(AcademicEvent event, Long id) {
        try {
            Field field = AcademicEvent.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(event, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @Test
    @DisplayName("새로운 이벤트들이 정상적으로 저장된다")
    void should_save_new_events_successfully() {
        // given
        when(academicEventQueryPort.findAllInEventUidsAsMap(anyList())).thenReturn(Map.of());

        // when
        academicEventDbSynchronizer.compareAndUpdateDb(testCalendarResult);

        // then
        verify(academicEventCommandPort, times(1)).saveAll(argThat(events ->
                events.size() == 2 &&
                        events.stream().anyMatch(e -> "UID001".equals(e.getEventUid())) &&
                        events.stream().anyMatch(e -> "UID002".equals(e.getEventUid()))
        ));
    }

    @Test
    @DisplayName("기존 이벤트가 업데이트되면 해당 값을 업데이트 한다.")
    void should_update_existing_events_by_deleting_old_and_saving_new() {
        // given
        when(academicEventQueryPort.findAllInEventUidsAsMap(anyList())).thenReturn(existingEventsMap);

        // when
        academicEventDbSynchronizer.compareAndUpdateDb(testCalendarResult);

        // then
        verify(academicEventCommandPort, times(1)).updateAll(argThat(events ->
                events.size() == 1 && events.get(0).getEventUid().equals("UID002")
        ));
        verify(academicEventCommandPort, times(1)).saveAll(argThat(events ->
                events.size() == 1 && events.get(0).getEventUid().equals("UID001")
        ));
    }

    @Test
    @DisplayName("sequence가 같거나 낮은 경우 업데이트하지 않는다")
    void should_not_update_when_sequence_is_same_or_lower() {
        // given
        AcademicEvent higherSequenceEvent = AcademicEvent.builder()
                .eventUid("UID002")
                .summary("높은 sequence 이벤트")
                .startTime(LocalDateTime.of(2024, 3, 2, 9, 0))
                .endTime(LocalDateTime.of(2024, 3, 2, 17, 0))
                .category("기본")
                .transparent(Transparent.TRANSPARENT)
                .sequence(2) // 새 이벤트보다 sequence가 높음
                .notifyEnabled(true)
                .build();

        Map<String, AcademicEvent> highSequenceMap = Map.of("UID002", higherSequenceEvent);
        when(academicEventQueryPort.findAllInEventUidsAsMap(anyList())).thenReturn(highSequenceMap);

        // when
        academicEventDbSynchronizer.compareAndUpdateDb(testCalendarResult);

        // then
        verify(academicEventCommandPort, times(1)).saveAll(argThat(events ->
                events.size() == 1 && events.get(0).getEventUid().equals("UID001")
        ));
        verify(academicEventCommandPort, never()).updateAll(anyList());
    }

    @Test
    @DisplayName("빈 이벤트 리스트에 대해서도 정상 처리된다")
    void should_handle_empty_event_list() {
        // given
        IcsCalendarResult emptyResult = new IcsCalendarResult(
                new IcsCalendarProperties(null, null, null, null),
                null,
                List.of()
        );
        when(academicEventQueryPort.findAllInEventUidsAsMap(anyList())).thenReturn(Map.of());

        // when
        academicEventDbSynchronizer.compareAndUpdateDb(emptyResult);

        // then
        verify(academicEventCommandPort, never()).saveAll(anyList());
        verify(academicEventCommandPort, never()).updateAll(anyList());
    }

    @Test
    @DisplayName("DB 조회 결과가 비어있어도 정상 처리된다")
    void should_handle_empty_existing_events() {
        // given
        when(academicEventQueryPort.findAllInEventUidsAsMap(anyList())).thenReturn(Map.of());

        // when
        academicEventDbSynchronizer.compareAndUpdateDb(testCalendarResult);

        // then
        verify(academicEventCommandPort, times(1)).saveAll(anyList());
        verify(academicEventCommandPort, never()).updateAll(anyList());
    }
}