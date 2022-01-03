package com.kustacks.kuring.kuapi.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
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

//    @JsonProperty("attachmentCnt")
//    private List<String> attachmentCnt;
//
//    @JsonProperty("attachments")
//    private List<String> attachments;

    @JsonProperty("isPersonal")
    private boolean isPersonal;

    @JsonProperty("likeCount")
    private int likeCount;

    @JsonProperty("isMyLike")
    private boolean isMyLike;
}
