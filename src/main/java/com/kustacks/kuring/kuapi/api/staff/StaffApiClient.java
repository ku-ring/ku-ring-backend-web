package com.kustacks.kuring.kuapi.api.staff;

import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.kuapi.api.ApiClient;
import com.kustacks.kuring.kuapi.staff.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;

import java.util.List;

public interface StaffApiClient extends ApiClient {

    int SCRAP_TIMEOUT = 10000;

    boolean support(DeptInfo deptInfo);

    List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException;
}
