package com.kustacks.kuring.auth.authentication;

import org.apache.logging.log4j.util.Strings;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class AuthorizationExtractor {
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCESS_TOKEN_TYPE = AuthorizationExtractor.class.getSimpleName() + ".ACCESS_TOKEN_TYPE";

    private AuthorizationExtractor() {
    }

    public static String extract(HttpServletRequest request, AuthorizationType type) {
        String typeToLowerCase = type.toLowerCase();
        int typeLength = typeToLowerCase.length();

        Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith(typeToLowerCase))) {
                String authHeaderValue = value.substring(typeLength).trim();
                request.setAttribute(ACCESS_TOKEN_TYPE, value.substring(0, typeLength).trim());
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }

        return Strings.EMPTY;
    }

    public static String extractAuthorizationValue(String value, AuthorizationType type) {
        String typeToLowerCase = type.toLowerCase();
        int typeLength = typeToLowerCase.length();

        if ((value.toLowerCase().startsWith(typeToLowerCase))) {
            String authHeaderValue = value.substring(typeLength).trim();
            int commaIndex = authHeaderValue.indexOf(',');
            if (commaIndex > 0) {
                authHeaderValue = authHeaderValue.substring(0, commaIndex);
            }
            return authHeaderValue;
        }

        return Strings.EMPTY;
    }
}
