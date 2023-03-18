package com.kustacks.kuring.worker.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LibraryNoticeDTO {
    @JsonProperty("id")
    private String id;

    @JsonProperty("seqNo")
    private String seqNo;

    @JsonProperty("bulletinCategory")
    private String bulletinCategory;

    @JsonProperty("bulletinTextHead")
    private String bulletinTextHead;

    @JsonProperty("bulletinState")
    private String bulletinState;

    @JsonProperty("isPrivate")
    private boolean isPrivate;

    @JsonProperty("title")
    private String title;

    @JsonProperty("writer")
    private String writer;

    @JsonProperty("dateCreated")
    private String dateCreated;

    @JsonProperty("lastUpdated")
    private String lastUpdated;

    @JsonProperty("hitCnt")
    private String hitCnt;

    @JsonProperty("replyCnt")
    private String replyCnt;

    @JsonProperty("commentCnt")
    private String commentCnt;

    @JsonProperty("isPersonal")
    private boolean isPersonal;

    @JsonProperty("likeCount")
    private int likeCount;

    @JsonProperty("isMyLike")
    private boolean isMyLike;
}
