package com.kustacks.kuring.worker.update.notice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KuisLoginInfo extends KuisInfo {

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
