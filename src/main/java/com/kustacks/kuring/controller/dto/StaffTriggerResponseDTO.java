package com.kustacks.kuring.controller.dto;

import lombok.Getter;

@Getter
public class StaffTriggerResponseDTO extends ResponseDTO {

    public StaffTriggerResponseDTO() {
        super(true, "성공", 200);
    }
}
