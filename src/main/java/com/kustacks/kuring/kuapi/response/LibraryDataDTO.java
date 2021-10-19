package com.kustacks.kuring.kuapi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class LibraryDataDTO {
    @JsonProperty("totalCount")
    private int totalCount;

    @JsonProperty("offset")
    private int offset;

    @JsonProperty("max")
    private int max;

    @JsonProperty("list")
    private List<LibraryNoticeDTO> list;
}
