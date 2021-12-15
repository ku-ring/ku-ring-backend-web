package com.kustacks.kuring.kuapi.api.staff;

import com.kustacks.kuring.kuapi.staff.deptinfo.DeptInfo;
import com.kustacks.kuring.kuapi.staff.deptinfo.art_design.CommunicationDesignDept;
import com.kustacks.kuring.kuapi.staff.deptinfo.art_design.LivingDesignDept;
import com.kustacks.kuring.kuapi.staff.deptinfo.real_estate.RealEstateDept;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class EachDeptStaffAPIClient implements StaffAPIClient {

    @Override
    public boolean support(DeptInfo deptInfo) {

        return !(deptInfo instanceof RealEstateDept) &&
                !(deptInfo instanceof LivingDesignDept) &&
                !(deptInfo instanceof CommunicationDesignDept);
    }

    @Override
    public List<Document> getHTML(DeptInfo deptInfo) throws IOException {

        UriComponentsBuilder urlBuilder;
        String url;
        List<Document> documents = new LinkedList<>();

        for (String pfForumId : deptInfo.getStaffScrapInfo().getPfForumId()) {
            urlBuilder = UriComponentsBuilder.fromUriString(deptInfo.getStaffScrapInfo().getBaseUrl()).queryParam("pfForumId", pfForumId);
            url = urlBuilder.toUriString();

            Document document = Jsoup.connect(url).timeout(SCRAP_TIMEOUT).get();

            Element pageNumHiddenInput = document.getElementById("totalPageCount");
            int totalPageNum = Integer.parseInt(pageNumHiddenInput.val());
            int pageNum = 1; // 이미 1페이지를 받아왔으므로 2페이지부터 호출하면됨

            while(true) {
                documents.add(document);

                if(++pageNum > totalPageNum) {
                    break;
                }

                document = Jsoup.connect(url)
                        .data("pageNum", String.valueOf(pageNum))
                        .post();
            }
        }

        return documents;
    }
}
