package com.kustacks.kuring.worker.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonNoticeFormatDTO {

    private String articleId;

    private String postedDate;

    private String updatedDate;

    private String subject;
}
