package com.kustacks.kuring.controller.dto;

import lombok.Getter;

@Getter
public class NoticeTriggerResponseDTO extends ResponseDTO {

    public NoticeTriggerResponseDTO() {
        super(true, "성공", 200);
    }
}
