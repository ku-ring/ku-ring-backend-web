package com.kustacks.kuring.alert.adapter.in.event;

import com.kustacks.kuring.alert.adapter.in.event.dto.AlertCreateEvent;
import com.kustacks.kuring.alert.adapter.in.event.dto.AlertDeleteEvent;
import com.kustacks.kuring.alert.application.port.in.AlertCommandUseCase;
import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertCommandEventListener {

    private final AlertCommandUseCase alertCommandUseCase;

    @EventListener
    public void createAlert(
            AlertCreateEvent event
    ) {
        AlertCreateCommand command = new AlertCreateCommand(
                event.title(), event.content(), event.alertTime()
        );

        alertCommandUseCase.addAlertSchedule(command);
    }

    @EventListener
    public void cancelAlert(
            AlertDeleteEvent event
    ) {
        alertCommandUseCase.cancelAlertSchedule(event.id());
    }
}
