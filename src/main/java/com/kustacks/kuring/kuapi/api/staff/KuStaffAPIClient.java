package com.kustacks.kuring.kuapi.api.staff;

import com.kustacks.kuring.kuapi.staff.deptinfo.DeptInfo;
import com.kustacks.kuring.kuapi.staff.deptinfo.art_design.CommunicationDesignDept;
import com.kustacks.kuring.kuapi.staff.deptinfo.art_design.LivingDesignDept;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class KuStaffAPIClient implements StaffAPIClient {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof LivingDesignDept || deptInfo instanceof CommunicationDesignDept;
    }

    @Override
    public List<Document> getHTML(DeptInfo deptInfo) throws IOException {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(deptInfo.getStaffScrapInfo().getBaseUrl());
        String url = urlBuilder.toUriString();

        Document document = Jsoup.connect(url).timeout(SCRAP_TIMEOUT).get();

        List<Document> documents = new LinkedList<>();
        documents.add(document);

        return documents;
    }
}
