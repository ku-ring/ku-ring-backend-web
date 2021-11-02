package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDTO extends ResponseDTO {

    @JsonProperty("dashboardUrl")
    private final String dashboardUrl = "/admin/dashboard";

    @Builder
    public LoginResponseDTO(boolean isSuccess, String resultMsg, int resultCode) {
        super(isSuccess, resultMsg, resultCode);
    }
}
