package com.kustacks.kuring.kuapi.api.staff;

import com.kustacks.kuring.kuapi.staff.deptinfo.DeptInfo;
import com.kustacks.kuring.kuapi.staff.deptinfo.real_estate.RealEstateDept;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class RealEstateStaffAPIClient implements StaffAPIClient {

    private final String PROXY_IP = "52.78.172.171";
    private final int PROXY_PORT = 80;

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof RealEstateDept;
    }

    @Override
    public List<Document> getHTML(DeptInfo deptInfo) throws IOException {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(deptInfo.getStaffScrapInfo().getBaseUrl());
        String url = urlBuilder.toUriString();
        Document document = Jsoup.connect(url).timeout(SCRAP_TIMEOUT).proxy(PROXY_IP, PROXY_PORT).get();

        List<Document> documents = new LinkedList<>();
        documents.add(document);

        return documents;
    }
}
