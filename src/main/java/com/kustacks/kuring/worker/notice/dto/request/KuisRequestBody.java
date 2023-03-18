package com.kustacks.kuring.worker.notice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class KuisRequestBody {

    private String urlEncodedString = null;

    public static String toUrlEncodedString(KuisRequestBody kuisRequestBody) {
        if(kuisRequestBody.urlEncodedString == null) {
            Field[] fields = kuisRequestBody instanceof KuisNoticeRequestBody ?
                    kuisRequestBody.getClass().getSuperclass().getDeclaredFields() :
                    kuisRequestBody.getClass().getDeclaredFields();

            StringBuilder sb = new StringBuilder();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    String encodedKey = URLEncoder.encode(field.getAnnotation(JsonProperty.class).value(), StandardCharsets.UTF_8);
                    String encodedValue = URLEncoder.encode(field.get(kuisRequestBody).toString(), StandardCharsets.UTF_8);

                    encodedKey = encodedKey.replaceAll("\\+", "%20");
                    encodedValue = encodedValue.replaceAll("\\+", "%20");

                    sb.append(encodedKey).append("=").append(encodedValue).append("&");
                } catch(IllegalAccessException e) {
                    return e.getMessage();
                }
            }

            sb.deleteCharAt(sb.length() - 1);

            kuisRequestBody.urlEncodedString = sb.toString();
        }

        return kuisRequestBody.urlEncodedString;
    }
}
