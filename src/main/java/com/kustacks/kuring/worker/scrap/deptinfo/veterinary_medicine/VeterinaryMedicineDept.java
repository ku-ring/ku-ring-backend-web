package com.kustacks.kuring.worker.scrap.deptinfo.veterinary_medicine;

import com.kustacks.kuring.worker.parser.notice.LatestPageNoticeHtmlParser;
import com.kustacks.kuring.worker.scrap.client.notice.LatestPageNoticeApiClient;
import com.kustacks.kuring.worker.scrap.client.notice.property.LatestPageNoticeProperties;
import com.kustacks.kuring.worker.scrap.deptinfo.NoticeScrapInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.scrap.deptinfo.StaffScrapInfo;

import java.util.List;

import static com.kustacks.kuring.notice.domain.DepartmentName.VET_MEDICINE;

@RegisterDepartmentMap(key = VET_MEDICINE)
public class VeterinaryMedicineDept extends VeterinaryMedicineCollege {

    public VeterinaryMedicineDept(
            LatestPageNoticeApiClient latestPageNoticeApiClient,
            LatestPageNoticeHtmlParser latestPageNoticeHtmlParser,
            LatestPageNoticeProperties latestPageNoticeProperties
    ) {
        super();
        this.noticeApiClient = latestPageNoticeApiClient;
        this.htmlParser = latestPageNoticeHtmlParser;
        this.latestPageNoticeProperties = latestPageNoticeProperties;
        List<Integer> siteIds = List.of(11135);
        this.staffScrapInfo = new StaffScrapInfo(VET_MEDICINE.getHostPrefix(), siteIds);
        this.noticeScrapInfo = new NoticeScrapInfo(VET_MEDICINE.getHostPrefix(), 948);
        this.departmentName = VET_MEDICINE;
    }
}
