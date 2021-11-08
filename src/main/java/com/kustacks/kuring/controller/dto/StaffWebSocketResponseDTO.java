package com.kustacks.kuring.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StaffWebSocketResponseDTO extends ResponseDTO {

    @JsonProperty("staffList")
    private List<StaffDTO> staffDTOList;

    public StaffWebSocketResponseDTO(List<StaffDTO> staffDTOList) {
        super(true, "성공", 200);
        this.staffDTOList = staffDTOList;
    }
}
