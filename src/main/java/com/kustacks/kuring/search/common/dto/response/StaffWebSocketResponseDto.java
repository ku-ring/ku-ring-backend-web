package com.kustacks.kuring.search.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustacks.kuring.common.dto.StaffDto;
import lombok.Getter;

import java.util.List;

@Getter
public class StaffWebSocketResponseDto extends WebSocketResponseDto {

    @JsonProperty("staffList")
    private List<StaffDto> staffDtoList;

    public StaffWebSocketResponseDto(List<StaffDto> staffDtoList) {
        super(true, "성공", 200, "staff");
        this.staffDtoList = staffDtoList;
    }
}
