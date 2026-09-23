package com.kustacks.kuring.new_message.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    NOTICE("notice"),
    ADMIN("admin"),
    ACADEMIC("academic"),
    CLUB("club");

    private final String value;
}
