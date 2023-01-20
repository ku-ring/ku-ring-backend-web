package com.kustacks.kuring.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StaffWebSocketResponseDTO extends WebSocketResponseDTO {

    @JsonProperty("staffList")
    private List<StaffDTO> staffDTOList;

    public StaffWebSocketResponseDTO(List<StaffDTO> staffDTOList) {
        super(true, "성공", 200, "staff");
        this.staffDTOList = staffDTOList;
    }
}
