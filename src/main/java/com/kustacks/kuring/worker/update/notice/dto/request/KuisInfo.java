package com.kustacks.kuring.worker.update.notice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class KuisInfo {

    private String urlEncodedString = null;

    public static String toUrlEncodedString(KuisInfo kuisInfo) {
        if(kuisInfo.urlEncodedString == null) {
            Field[] fields = kuisInfo instanceof KuisNoticeInfo ?
                    kuisInfo.getClass().getSuperclass().getDeclaredFields() :
                    kuisInfo.getClass().getDeclaredFields();

            StringBuilder sb = new StringBuilder();
            for (Field field : fields) {
                if(field.getName().equals("categoryName") || field.getName().equals("noticeApiClient")) continue;

                try {
                    field.setAccessible(true);
                    String encodedKey = URLEncoder.encode(field.getAnnotation(JsonProperty.class).value(), StandardCharsets.UTF_8);
                    String encodedValue = URLEncoder.encode(field.get(kuisInfo).toString(), StandardCharsets.UTF_8);

                    encodedKey = encodedKey.replaceAll("\\+", "%20");
                    encodedValue = encodedValue.replaceAll("\\+", "%20");

                    sb.append(encodedKey).append("=").append(encodedValue).append("&");
                } catch(IllegalAccessException e) {
                    return e.getMessage();
                }
            }

            sb.deleteCharAt(sb.length() - 1);

            kuisInfo.urlEncodedString = sb.toString();
        }

        return kuisInfo.urlEncodedString;
    }
}
