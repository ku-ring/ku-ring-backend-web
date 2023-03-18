package com.kustacks.kuring.worker.client.staff;

import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.client.ApiClient;
import com.kustacks.kuring.worker.update.staff.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;

import java.util.List;

public interface StaffApiClient extends ApiClient {

    int SCRAP_TIMEOUT = 10000;

    boolean support(DeptInfo deptInfo);

    List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException;
}
