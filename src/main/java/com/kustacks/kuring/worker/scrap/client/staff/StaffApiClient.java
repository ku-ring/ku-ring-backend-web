package com.kustacks.kuring.worker.scrap.client.staff;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;

import java.util.List;

public interface StaffApiClient {

    boolean support(DeptInfo deptInfo);

    List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException;
}
