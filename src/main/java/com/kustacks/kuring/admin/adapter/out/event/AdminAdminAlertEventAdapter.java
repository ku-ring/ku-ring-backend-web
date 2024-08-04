package com.kustacks.kuring.admin.adapter.out.event;

import com.kustacks.kuring.admin.application.port.out.AdminAlertEventPort;
import com.kustacks.kuring.alert.adapter.in.event.dto.AlertCreateEvent;
import com.kustacks.kuring.alert.adapter.in.event.dto.AlertDeleteEvent;
import com.kustacks.kuring.common.domain.Events;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminAdminAlertEventAdapter implements AdminAlertEventPort {

    @Override
    public void addAlertSchedule(String title, String content, LocalDateTime alertTime) {
        Events.raise(new AlertCreateEvent(title, content, alertTime));
    }

    @Override
    public void cancelAlertSchedule(Long id) {
        Events.raise(new AlertDeleteEvent(id));
    }
}
