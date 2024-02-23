package com.kustacks.kuring.worker.scrap.client.staff;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.scrap.client.JsoupClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@Component
public class RealEstateStaffApiClient implements StaffApiClient {

    private static final int STAFF_SCRAP_TIMEOUT = 300000;
    private final JsoupClient jsoupClient;

    @Value("${staff.real-estate-url}")
    private String baseUrl;

    public RealEstateStaffApiClient(JsoupClient proxyJsoupClient) {
        this.jsoupClient = proxyJsoupClient;
    }

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof RealEstateDept;
    }

    @Override
    public List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException {
        String url = buildProfessorInfoUrl();
        Document document = getDocumentByUrl(url);
        return List.of(document);
    }

    private Document getDocumentByUrl(String url) {
        try {
            return jsoupClient.get(url, STAFF_SCRAP_TIMEOUT);
        } catch(IOException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_SCRAP, e);
        }
    }

    private String buildProfessorInfoUrl() {
        return UriComponentsBuilder.fromUriString(baseUrl).toUriString();
    }
}
