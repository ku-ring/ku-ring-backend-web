package com.kustacks.kuring.worker.update.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class KuisNoticeResponseDto extends NoticeResponseDTO {
    @JsonProperty("DS_LIST")
    List<KuisNoticeDto> kuisNoticeDtoList;
}
