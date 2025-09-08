package com.kustacks.kuring.notice.application.port.in.dto;

public record NoticeRangeLookupCommand(
        String type,
        String department,
        Boolean important,
        Boolean graduate,
        int page,
        int size
) {
    public boolean isImportant() {
        return this.important;
    }
}
