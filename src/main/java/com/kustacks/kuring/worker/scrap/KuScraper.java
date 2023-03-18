package com.kustacks.kuring.worker.scrap;

import com.kustacks.kuring.worker.update.staff.deptinfo.DeptInfo;

import java.io.IOException;
import java.util.List;

public interface KuScraper<T> {

    int RETRY_PERIOD = 1000 * 60; // 1분후에 실패한 크론잡 재시도

    List<T> scrap(DeptInfo deptInfo) throws IOException;
}
