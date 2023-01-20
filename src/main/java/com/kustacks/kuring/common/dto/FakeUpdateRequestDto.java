package com.kustacks.kuring.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FakeUpdateRequestDto {

    private String articleId;

    private String postedDate;

    private String subject;

    private String category;

    private String token;

    private String auth;
}
