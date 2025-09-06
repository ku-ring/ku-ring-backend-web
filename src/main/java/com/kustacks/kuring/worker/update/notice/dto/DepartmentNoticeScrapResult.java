package com.kustacks.kuring.worker.update.notice.dto;

import com.kustacks.kuring.worker.dto.ComplexNoticeFormatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentNoticeScrapResult {
    private ComplexNoticeFormatDto complexNoticeFormatDto;
    private boolean isGrad;
}
