package com.kustacks.kuring.admin.application.port.out;

import java.time.LocalDateTime;

public interface AdminAlertEventPort {
    void addAlertSchedule(String title, String content, LocalDateTime alertTime);

    void cancelAlertSchedule(Long id);
}
