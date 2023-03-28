package com.kustacks.kuring.worker.client.staff;

import com.kustacks.kuring.common.error.ErrorCode;
import com.kustacks.kuring.common.error.InternalLogicException;
import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.CommunicationDesignDept;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.LivingDesignDept;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class KuStaffApiClient implements StaffApiClient {

    private static final int STAFF_SCRAP_TIMEOUT = 30000;

    private final Map<String, String> urlMap;
    private final JsoupClient jsoupClient;

    public KuStaffApiClient(
            @Value("${staff.living-design-url}") String livingDesignUrl,
            @Value("${staff.communication-design-url}") String communicationDesignUrl,
            JsoupClient normalJsoupClient)
    {
        urlMap = new HashMap<>();
        urlMap.put(DepartmentName.COMM_DESIGN.getKorName(), communicationDesignUrl);
        urlMap.put(DepartmentName.LIVING_DESIGN.getKorName(), livingDesignUrl);
        this.jsoupClient = normalJsoupClient;
    }

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof LivingDesignDept || deptInfo instanceof CommunicationDesignDept;
    }

    @Override
    public List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(urlMap.get(deptInfo.getDeptName()));
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
