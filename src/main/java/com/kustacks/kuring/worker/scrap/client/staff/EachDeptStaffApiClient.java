package com.kustacks.kuring.worker.scrap.client.staff;

import com.kustacks.kuring.common.exception.InternalLogicException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.worker.scrap.client.NormalJsoupClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.CommunicationDesignDept;
import com.kustacks.kuring.worker.scrap.deptinfo.art_design.LivingDesignDept;
import com.kustacks.kuring.worker.scrap.deptinfo.real_estate.RealEstateDept;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class EachDeptStaffApiClient implements StaffApiClient {

    private static final int STAFF_SCRAP_TIMEOUT = 30000;
    private final NormalJsoupClient jsoupClient;

    @Value("${staff.each-dept-url}")
    private String baseUrl;

    public EachDeptStaffApiClient(NormalJsoupClient normalJsoupClient) {
        this.jsoupClient = normalJsoupClient;
    }

    @Override
    public boolean support(DeptInfo deptInfo) {
        return !(deptInfo instanceof RealEstateDept) &&
                !(deptInfo instanceof LivingDesignDept) &&
                !(deptInfo instanceof CommunicationDesignDept);
    }

    @Override
    public List<Document> getHTML(DeptInfo deptInfo) throws InternalLogicException {
        return deptInfo.getProfessorForumIds().stream()
                .flatMap(professorForumId -> getProfessorHtmlById(professorForumId).stream())
                .toList();
    }

    private List<Document> getProfessorHtmlById(String professorForumId) {
        LinkedList<Document> documents = new LinkedList<>();

        String url = buildProfessorInfoUrl(professorForumId);
        Document document = getDocument(url);
        documents.add(document);

        int totalPageNum = getTotalPageNumber(document);
        for (int pageNumber = 2; pageNumber <= totalPageNum; pageNumber++) {
            documents.add(parseDocumentByPageNumber(url, pageNumber));
        }

        return documents;
    }

    private Document parseDocumentByPageNumber(String url, int pageNumber) {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("pageNum", String.valueOf(pageNumber));
            return jsoupClient.post(url, STAFF_SCRAP_TIMEOUT, requestBody);
        } catch (IOException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_SCRAP, e);
        }
    }

    private static int getTotalPageNumber(Document document) {
        Element pageNumHiddenInput = document.getElementById("totalPageCount");
        return Integer.parseInt(pageNumHiddenInput.val());
    }

    private Document getDocument(String url) {
        try {
            return jsoupClient.get(url, STAFF_SCRAP_TIMEOUT);
        } catch (IOException e) {
            throw new InternalLogicException(ErrorCode.STAFF_SCRAPER_CANNOT_SCRAP, e);
        }
    }

    private String buildProfessorInfoUrl(String pfForumId) {
        return UriComponentsBuilder.fromUriString(baseUrl).queryParam("pfForumId", pfForumId).toUriString();
    }
}
