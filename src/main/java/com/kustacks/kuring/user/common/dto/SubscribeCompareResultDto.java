package com.kustacks.kuring.user.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SubscribeCompareResultDto <T> {

    private List<T> savedNameList;
    private List<T> deletedNameList;
}
