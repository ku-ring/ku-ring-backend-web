package com.kustacks.kuring.worker.scrap.parser.notice;

import lombok.Getter;

import java.util.List;

@Getter
public class RowsDto {

    private final List<String[]> importantRowList;
    private final List<String[]> normalRowList;

    public RowsDto(List<String[]> importantRowList, List<String[]> normalRowList) {
        this.importantRowList = importantRowList;
        this.normalRowList = normalRowList;
    }
}
