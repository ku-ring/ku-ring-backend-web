package com.kustacks.kuring.alert.application.port.out.dto;

import com.kustacks.kuring.alert.domain.Alert;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime alertTime;

    private AlertDto(Long id, String title, String content, LocalDateTime alertTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.alertTime = alertTime;
    }

    public static AlertDto from(Alert alert) {
        return new AlertDto(alert.getId(), alert.getTitle(), alert.getContent(), alert.getAlertTime());
    }
}
