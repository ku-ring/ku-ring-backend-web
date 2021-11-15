package com.kustacks.kuring.kuapi.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KuisLoginRequestBody extends KuisRequestBody {
    @JsonProperty("Wke8,1")
    private final String v1 = "Ie3,jd";

    @JsonProperty("s=83_41")
    private final String v2 = "83+38";

    @JsonProperty("n23,@d")
    private final String v3 = "H_3h,e";

    @JsonProperty("Lwu3=d")
    private final String v4 = "2gd$73";

    @JsonProperty("kd93#!c")
    private final String v5 = "KI&3jh";

    @JsonProperty("Hem=7_w$")
    private final String v6 = "mi2e_>";

    @JsonProperty("G=e %37")
    private final String v7 = "Jd 338#";

    @JsonProperty("D37=!f3")
    private final String v8 = "K#f8kf4M";

    @JsonProperty("3Y2&2e")
    private final String v9 = "2=3%37";

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

    @JsonProperty("(3hj2D#")
    private final String v14 = ")8ne_=12";
}
