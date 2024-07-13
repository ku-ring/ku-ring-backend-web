package com.kustacks.kuring.ai.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserQuestionRequest(
        @NotBlank @Size(min = 5, max = 256) String question
) {
}
