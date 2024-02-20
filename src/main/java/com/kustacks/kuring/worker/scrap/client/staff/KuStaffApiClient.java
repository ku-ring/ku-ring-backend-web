package com.kustacks.kuring.worker.scrap.client.staff;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.scrap.client.JsoupClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.CommunicationDesignDept;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.LivingDesignDept;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
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
        this.urlMap = new HashMap<>();
        this.urlMap.put(DepartmentName.COMM_DESIGN.getKorName(), communicationDesignUrl);
        this.urlMap.put(DepartmentName.LIVING_DESIGN.getKorName(), livingDesignUrl);
        this.jsoupClient = normalJsoupClient;
    }

    @Override
    public boolean support(DeptInfo deptInfo) {
        return deptInfo instanceof LivingDesignDept || deptInfo instanceof CommunicationDesignDept;
    }

    @Override
    public List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException {
        String url = buildProfessorInfoUrl(deptInfo);
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

    private String buildProfessorInfoUrl(DeptInfo deptInfo) {
        return UriComponentsBuilder.fromUriString(urlMap.get(deptInfo.getDeptName())).toUriString();
    }
}
