package com.kustacks.kuring.notice.domain;

public enum BadWordCategory {

    PROFANITY("욕설"),
    HATE_SPEECH("혐오발언"),
    SPAM("스팸"),
    ADULT("성인"),
    VIOLENCE("폭력"),
    DRUG("마약"),
    GAMBLING("도박"),
    PERSONAL_INFO("개인정보"),
    OTHER("기타");

    private final String description;

    BadWordCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
