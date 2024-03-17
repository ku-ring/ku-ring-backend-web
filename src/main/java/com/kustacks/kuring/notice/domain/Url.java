package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.common.exception.InternalLogicException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Url {

    /**
     * Regular Expression by RFC 3986 for URI Validation
     * @link https://datatracker.ietf.org/doc/html/rfc3986#appendix-B
     * @author jiwoo
     */
    private static final String REGEX_SCHEME = "[A-Za-z][+-.\\w^_]*:";

    // Example: "//".
    private static final String REGEX_AUTHORATIVE_DECLARATION = "/{2}";

    // Optional component. Example: "suzie:abc123@". The use of the format "user:password" is deprecated.
    private static final String REGEX_USERINFO = "(?:\\S+(?::\\S*)?@)?";

    // Examples: "fitbit.com", "22.231.113.64".
    private static final String REGEX_HOST = "(?:" +  // @Author = http://www.regular-expressions.info/examples.html
            // IP address
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
            "|" +  // host name
            "(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)" +  // domain name
            "(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*" +  // TLD identifier must have >= 2 characters
            "(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))";

    // Example: ":8042".
    private static final String REGEX_PORT = "(?::\\d{2,5})?";

    //Example: "/user/heartrate?foo=bar#element1".
    private static final String REGEX_RESOURCE_PATH = "(?:/\\S*)?";

    private static final String REGEX_URL = "^(?:(?:" + REGEX_SCHEME + REGEX_AUTHORATIVE_DECLARATION + ")?" +
            REGEX_USERINFO + REGEX_HOST + REGEX_PORT + REGEX_RESOURCE_PATH + ")$";

    private static final Pattern compiledUrlPattern = Pattern.compile(REGEX_URL);

    @Column(name = "url", length = 256, nullable = false)
    private String value;

    public Url(String fullUrl) {
        if (!this.isValidUrl(fullUrl)) {
            throw new InternalLogicException(ErrorCode.DOMAIN_CANNOT_CREATE);
        }

        this.value = fullUrl;
    }

    private boolean isValidUrl(String fullUrl) {
        return !Objects.isNull(fullUrl) && patternMatches(fullUrl);
    }

    private boolean patternMatches(String targetUrl) {
        return compiledUrlPattern
                .matcher(targetUrl)
                .matches();
    }
}
