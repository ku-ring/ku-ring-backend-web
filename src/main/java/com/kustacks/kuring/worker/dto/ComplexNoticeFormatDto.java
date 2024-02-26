package com.kustacks.kuring.worker.dto;

import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComplexNoticeFormatDto {

    List<CommonNoticeFormatDto> importantNoticeList;
    List<CommonNoticeFormatDto> normalNoticeList;

    public int getNormalNoticeSize() {
        return normalNoticeList.size();
    }
}
