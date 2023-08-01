package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.exception.DomainLogicException;
import com.kustacks.kuring.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Url {

    private static final String REGEX_PATTERN = "^((http|https)://)?([a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(/.*)?)$";

    @Column(name = "url", length = 256, nullable = false)
    private String value;

    public Url(String fullUrl) {
        if (!this.isValidUrl(fullUrl)) {
            throw new DomainLogicException(ErrorCode.DOMAIN_CANNOT_CREATE);
        }

        this.value = fullUrl;
    }

    private boolean isValidUrl(String fullUrl) {
        return !Objects.isNull(fullUrl) && patternMatches(fullUrl, REGEX_PATTERN);
    }

    private boolean patternMatches(String targetUrl, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(targetUrl)
                .matches();
    }
}
