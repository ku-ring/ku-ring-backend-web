package com.kustacks.kuring.worker.client.staff;

import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class RealEstateStaffApiClient implements StaffApiClient {

    private static final int STAFF_SCRAP_TIMEOUT = 30000;

    @Value("${staff.real-estate-url}")
    private String baseUrl;

    private final JsoupClient jsoupClient;

    public RealEstateStaffApiClient(JsoupClient proxyJsoupClient) {
        this.jsoupClient = proxyJsoupClient;
    }

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof RealEstateDept;
    }

    @Override
    public List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        String url = urlBuilder.toUriString();

        Document document;
        try {
            document = jsoupClient.get(url, STAFF_SCRAP_TIMEOUT);
        } catch(IOException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_SCRAP, e);
        }

        List<Document> documents = new LinkedList<>();
        documents.add(document);

        return documents;
    }
}
