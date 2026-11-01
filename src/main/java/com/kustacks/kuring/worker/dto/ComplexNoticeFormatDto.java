package com.kustacks.kuring.worker.dto;

import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ComplexNoticeFormatDto {

    List<CommonNoticeFormatDto> importantNoticeList;
    List<CommonNoticeFormatDto> normalNoticeList;

    public ComplexNoticeFormatDto(
            List<CommonNoticeFormatDto> importantNoticeList,
            List<CommonNoticeFormatDto> normalNoticeList
    ) {
        this.importantNoticeList = importantNoticeList;
        this.normalNoticeList = normalNoticeList;
        this.deleteDuplicatedArticleInOnlyNormal();
    }

    public int getNormalNoticeSize() {
        return normalNoticeList.size();
    }

    public List<CommonNoticeFormatDto> getAllNoticeList() {
        List<CommonNoticeFormatDto> allNotices = new ArrayList<>(importantNoticeList);
        allNotices.addAll(normalNoticeList);
        return allNotices;
    }

    private void deleteDuplicatedArticleInOnlyNormal() {
        for(CommonNoticeFormatDto importantNoticeDto : importantNoticeList) {
            normalNoticeList.removeIf(notice -> notice.getArticleId().equals(importantNoticeDto.getArticleId()));
        }
    }
}
