package com.kustacks.kuring.kuapi.notice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class KuisNoticeRequestBody extends KuisRequestBody {
    @JsonProperty("@d1#forum_id")
    protected String v1;

    @JsonProperty("@d1#subject_code")
    protected String v2;

    @JsonProperty("@d#")
    protected String v3 = "@d1#";

    @JsonProperty("@d1#")
    protected String v4 = "dmBoardNoticeParam";

    @JsonProperty("@d1#tp")
    protected String v5 = "dm";

    public KuisNoticeRequestBody(String v1, String v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
}
