package com.kustacks.kuring.calendar.domain;

public enum Transparent {
    OPAQUE, //바쁨(중요일정)
    TRANSPARENT //바쁘지 않음(미중요일정)
    ;

    public static Transparent valueOfString(String transp) {
        transp = transp.trim().toUpperCase();

        return switch (transp) {
            case "TRANSPARENT" -> TRANSPARENT;
            case "OPAQUE" -> OPAQUE;
            default -> null;
        };
    }
}
