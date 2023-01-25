package com.kustacks.kuring.common.utils.converter;

import org.springframework.stereotype.Component;

@Component
public class YmdhmsToYmdConverter implements DateConverter {

    @Override
    public String convert(String ymdhms) {
        String[] splited = ymdhms.split(" ");
        String ymd = splited[0];
        return ymd.replaceAll("-", "");
    }
}
