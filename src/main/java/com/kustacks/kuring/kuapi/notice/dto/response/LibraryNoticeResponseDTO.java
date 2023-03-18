package com.kustacks.kuring.kuapi.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

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

    public int getTotalCount() {
        return this.data.getTotalCount();
    }

    public List<LibraryNoticeDTO> getList() {
        return this.data.getList();
    }
}
