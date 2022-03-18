package com.kustacks.kuring.service.utils;

import com.kustacks.kuring.persistence.notice.Notice;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

@Component
public class ObjectComparator {

    public static Comparator<Notice> NoticeDateComparator
            = (a, b) -> {

                boolean isALocalDateTime = a.getCategory().getName().equals("library");
                boolean isBLocalDateTime = b.getCategory().getName().equals("library");
                DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                LocalDateTime adt;
                LocalDateTime bdt;

                if(isALocalDateTime && isBLocalDateTime) {

                    adt = LocalDateTime.parse(a.getPostedDate(), localDateTimeFormatter);
                    bdt = LocalDateTime.parse(b.getPostedDate(), localDateTimeFormatter);

                } else if(!isALocalDateTime && isBLocalDateTime) {

                    adt = LocalDate.parse(a.getPostedDate(), localDateFormatter).atStartOfDay();
                    bdt = LocalDateTime.parse(b.getPostedDate(), localDateTimeFormatter);

                } else if(isALocalDateTime && !isBLocalDateTime) {

                    adt = LocalDateTime.parse(a.getPostedDate(), localDateTimeFormatter);
                    bdt = LocalDate.parse(b.getPostedDate(), localDateFormatter).atStartOfDay();

                } else {

                    adt = LocalDate.parse(a.getPostedDate(), localDateFormatter).atStartOfDay();
                    bdt = LocalDate.parse(b.getPostedDate(), localDateFormatter).atStartOfDay();

                }

                return bdt.isAfter(adt) ? 1 : 0;
            };
}
