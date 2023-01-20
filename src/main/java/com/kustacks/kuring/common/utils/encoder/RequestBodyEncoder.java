package com.kustacks.kuring.common.utils.encoder;

import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class RequestBodyEncoder implements Encoder {

    private final Map<String, String> uniCodeMap;

    public RequestBodyEncoder() {
        uniCodeMap = new HashMap<>();
        uniCodeMap.put("\\b", "\u0008");
        uniCodeMap.put("\\f", "\u000C");
    }

    @Override
    public String encode(String str) {

        StringBuilder sb = new StringBuilder();
        for(int i=0; i<str.length(); ++i) {
            char c = str.charAt(i);
            if(c == '\\') {
                if(i < str.length() - 1) {
                    String spec = str.substring(i, i+2);
                    String unicode = uniCodeMap.get(spec);
                    sb.append(unicode);
                    ++i;
                } else {
                    sb.append(URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8));
                }
            } else {
                sb.append(URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8));
            }
        }

        return sb.toString();
    }
}
