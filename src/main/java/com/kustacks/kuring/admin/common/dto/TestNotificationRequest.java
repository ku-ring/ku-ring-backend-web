package com.kustacks.kuring.admin.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestNotificationRequest {

    private String category;
    private String subject;
    private String articleId;
}
