package com.kustacks.kuring.kuapi.scrap;

import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.deptinfo.DeptInfo;

import java.io.IOException;
import java.util.List;

public interface KuScraper<T> {

    int RETRY_PERIOD = 1000 * 60; // 1분후에 실패한 크론잡 재시도

    List<T> scrap(DeptInfo deptInfo) throws InternalLogicException;
}
