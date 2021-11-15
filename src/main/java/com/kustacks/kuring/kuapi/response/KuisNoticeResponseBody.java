package com.kustacks.kuring.kuapi.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class KuisNoticeResponseBody {
    @JsonProperty("DS_LIST")
    List<KuisNoticeDTO> kuisNoticeDTOList;
}
