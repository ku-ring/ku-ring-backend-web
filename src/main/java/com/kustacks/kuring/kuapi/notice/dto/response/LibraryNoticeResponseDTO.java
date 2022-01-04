package com.kustacks.kuring.kuapi.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LibraryNoticeResponseDTO extends NoticeResponseDTO {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private LibraryDataDTO data;
}
