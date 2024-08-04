package com.kustacks.kuring.alert.application.port.in;

import com.kustacks.kuring.alert.application.port.in.dto.AlertCreateCommand;

public interface AlertCommandUseCase {

    void initAlertSchedule();

    void addAlertSchedule(AlertCreateCommand command);

    void cancelAlertSchedule(Long id);

    void sendAlert(Long id);
}
