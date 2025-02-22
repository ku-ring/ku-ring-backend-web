package com.kustacks.kuring.common.data;

public class Cursor {

    private static final String AUTO_ADJUST_DEFAULT_CURSOR = "1";
    private static final String DEFAULT_CURSOR = "0";

    private final String stringCursor;

    private Cursor(String stringCursor) {
        this.stringCursor = stringCursor;
    }

    public String getStringCursor() {
        return stringCursor;
    }

    public static Cursor from(String cursor) {
        if (cursor != null && cursor.equals(AUTO_ADJUST_DEFAULT_CURSOR)) {
            return new Cursor(DEFAULT_CURSOR);
        }

        if (cursor == null) {
            return new Cursor(DEFAULT_CURSOR);
        }

        return new Cursor(cursor);
    }
}
