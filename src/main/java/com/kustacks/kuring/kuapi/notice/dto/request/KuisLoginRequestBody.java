package com.kustacks.kuring.kuapi.notice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KuisLoginRequestBody extends KuisRequestBody {
//    @JsonProperty("Imu^42d")
//    private final String v1 = "Ke\b'eh3";
//
//    @JsonProperty("!wen%3d=")
//    private final String v2 = "Le<n3:";
//
//    @JsonProperty("O$e8;E-")
//    private final String v3 = "-83 ,3)";
//
//    @JsonProperty("ie&ke(W")
//    private final String v4 = "kn@#\u000c2";
//
//    @JsonProperty("Pd?w4r")
//    private final String v5 = "Ek^]ey!";
//
//    @JsonProperty("Je\r8<3")
//    private final String v6 = "ue>vei";
//
//    @JsonProperty("@93jdq")
//    private final String v14 = "+20Xdu)";
//
//    @JsonProperty("jkel|eg")
//    private final String v7 = "%[YrN7";
//
//    @JsonProperty("Mi&L,yo")
//    private final String v8 = "Ser/t7";
//
//    @JsonProperty("Le83';hg")
//    private final String v9 = "Ne*W_";

    @JsonProperty("@d1#tp")
    private final String v10 = "dm";

    @JsonProperty("@d1#SINGLE_ID")
    @Value("${auth.id}")
    private String id;

    @JsonProperty("@d1#PWD")
    @Value("${auth.password}")
    private String password;

    @JsonProperty("@d1#default.locale")
    private final String v11 = "ko";

    @JsonProperty("@d1#")
    private final String v12 = "dsParam";

    @JsonProperty("@d#")
    private final String v13 = "@d1#";
}
