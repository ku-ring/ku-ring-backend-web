package com.kustacks.kuring.worker.scrap.client.staff;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.scrap.client.NormalJsoupClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class EachDeptStaffApiClient implements StaffApiClient {

    private static final int STAFF_SCRAP_TIMEOUT = 30000;
    private final NormalJsoupClient jsoupClient;

    @Value("${staff.each-dept-url}")
    private String baseUrl;

    public EachDeptStaffApiClient(NormalJsoupClient normalJsoupClient) {
        this.jsoupClient = normalJsoupClient;
    }

    /*
    만약, 학과별로 다른 API Client를 구성해야 한다면 support 구현 필요.
    현재는 교직원 스크랩을 위한 모든 API 클래이언트 스펙 동일, 파싱에서 분리
    [2024.11.28 김한주]
     */
    @Override
    public boolean support(DeptInfo deptInfo) {
        return true;
    }

    @Override
    public List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException {
        return deptInfo.getStaffSiteIds().stream()
                .flatMap(siteId -> getProfessorHtmlByDeptAndSiteId(deptInfo.getStaffSiteName(), siteId).stream())
                .toList();
    }

    private List<Document> getProfessorHtmlByDeptAndSiteId(String siteName, int siteId) {
        LinkedList<Document> documents = new LinkedList<>();

        String url = buildDeptStaffPageUrl(siteName, siteId);
        Document document = getDocument(url);
        documents.add(document);
        return documents;
    }


    private Document getDocument(String url) {
        try {
            return jsoupClient.get(url, STAFF_SCRAP_TIMEOUT);
        } catch (IOException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_SCRAP, e);
        }
    }

    private String buildDeptStaffPageUrl(String department, int siteId) {
        return baseUrl.replace("{department}", department)
                .replace("{siteId}", String.valueOf(siteId));
    }
}
