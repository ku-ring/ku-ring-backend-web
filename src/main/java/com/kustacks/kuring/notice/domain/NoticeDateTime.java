package com.kustacks.kuring.notice.domain;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeDateTime {

    private static final String REGEX_DATE = "^\\d{4}-\\d{2}-\\d{2}$";
    private static final String REGEX_DATE_TIME = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    private static final Pattern compiledDatePattern = Pattern.compile(REGEX_DATE);
    private static final Pattern compiledDateTimePattern = Pattern.compile(REGEX_DATE_TIME);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.KOREA);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(" HH:mm:ss").withLocale(Locale.KOREA);

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "posted_dt", length = 32, nullable = false)
    private LocalDateTime postedDate;

    @Getter(AccessLevel.PUBLIC)
    @Column(name = "updated_dt", length = 32)
    private LocalDateTime updatedDate;

    public NoticeDateTime(String postedDate, String updatedDate) {
        postedDate = postedDate.trim();
        updatedDate = updatedDate.trim();

        if (isValidDate(postedDate, updatedDate)) {
            initDate(postedDate, updatedDate);
            return;
        }

        if(isValidDateTime(postedDate, updatedDate)) {
            initDateTime(postedDate, updatedDate);
            return;
        }

        throw new InternalLogicException(ErrorCode.DOMAIN_CANNOT_CREATE);
    }

    public String postedDateStr() {
        return this.postedDate.format(dateTimeFormatter);
    }

    public String updatedDateStr() {
        return this.updatedDate.format(dateTimeFormatter);
    }

    private void initDateTime(String postedDate, String updatedDate) {
        this.postedDate = LocalDateTime.parse(postedDate, dateTimeFormatter);
        this.updatedDate = LocalDateTime.parse(updatedDate, dateTimeFormatter);
    }

    private void initDate(String postedDate, String updatedDate) {
        LocalTime now = LocalTime.now();
        postedDate += now.format(timeFormatter);
        updatedDate += now.format(timeFormatter);
        initDateTime(postedDate, updatedDate);
    }

    private boolean isValidDate(String postedDate, String updatedDate) {
        return compiledDatePattern.matcher(postedDate).matches() &&
                compiledDatePattern.matcher(updatedDate).matches();
    }

    private boolean isValidDateTime(String postedDate, String updatedDate) {
        return compiledDateTimePattern.matcher(postedDate).matches() &&
                compiledDateTimePattern.matcher(updatedDate).matches();
    }
}
