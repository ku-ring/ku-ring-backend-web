package com.kustacks.kuring.user.application.port.in.dto;

import java.util.List;

public record UserSubscribeCompareResult<T>(
        List<T> savedNameList,
        List<T> deletedNameList
) {
}
