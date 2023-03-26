package com.kustacks.kuring.worker.client.staff;

import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;

import java.util.List;

public interface StaffApiClient {

    int SCRAP_TIMEOUT = 100000;

    boolean support(DeptInfo deptInfo);

    List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException;
}
