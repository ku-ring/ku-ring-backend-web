package com.kustacks.kuring.worker.update.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class LibraryNoticeResponseDto extends NoticeResponseDTO {
    @JsonProperty("success")
    private boolean success;

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private LibraryDataDto data;

    public int getTotalCount() {
        return this.data.getTotalCount();
    }

    public List<LibraryNoticeDto> getList() {
        return this.data.getList();
    }
}
