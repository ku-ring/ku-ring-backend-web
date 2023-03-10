package com.kustacks.kuring.feedback.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaveFeedbackRequest {

    @NotBlank
    private String id;

    @NotBlank
    private String content;
}

