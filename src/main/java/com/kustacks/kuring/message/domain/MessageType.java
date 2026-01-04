package com.kustacks.kuring.message.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    NOTICE("notice"),
    ADMIN("admin"),
    ACADEMIC("academic");

    private final String value;
}
