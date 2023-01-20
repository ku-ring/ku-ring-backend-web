package com.kustacks.kuring.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminMessageDto extends FcmMessageDto {

    private String title;

    private String body;

    public AdminMessageDto(String title, String body) {
        this.type = "admin";
        this.title = title;
        this.body = body;
    }
}

