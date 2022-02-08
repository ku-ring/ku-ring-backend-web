package com.kustacks.kuring.kuapi.scrap;

import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.kuapi.api.notice.NoticeAPIClient;
import com.kustacks.kuring.kuapi.deptinfo.DeptInfo;
import com.kustacks.kuring.kuapi.deptinfo.art_design.ArtDesignCollege;
import com.kustacks.kuring.kuapi.deptinfo.art_design.LivingDesignDept;
import com.kustacks.kuring.kuapi.deptinfo.business.BusinessCollege;
import com.kustacks.kuring.kuapi.deptinfo.education.EducationCollege;
import com.kustacks.kuring.kuapi.deptinfo.engineering.AdvancedIndustrialFusionDept;
import com.kustacks.kuring.kuapi.deptinfo.engineering.EngineeringCollege;
import com.kustacks.kuring.kuapi.deptinfo.ku_integrated_science.KuIntegratedScienceCollege;
import com.kustacks.kuring.kuapi.deptinfo.real_estate.RealEstateDept;
import com.kustacks.kuring.kuapi.deptinfo.sanghuo_biology.SanghuoBiologyCollege;
import com.kustacks.kuring.kuapi.deptinfo.sanghuo_elective.SanghuoCollege;
import com.kustacks.kuring.kuapi.deptinfo.social_science.PoliticalScienceDept;
import com.kustacks.kuring.kuapi.deptinfo.social_science.SocialSciencesCollege;
import com.kustacks.kuring.kuapi.deptinfo.veterinary_medicine.VeterinaryMedicineCollege;
import com.kustacks.kuring.kuapi.notice.dto.response.CommonNoticeFormatDTO;
import com.kustacks.kuring.kuapi.scrap.parser.NoticeHTMLParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class NoticeScraper implements KuScraper<CommonNoticeFormatDTO> {

    private final Map<DeptInfo, NoticeAPIClient<Document, DeptInfo>> deptInfoNoticeAPIClientMap;
    private final Map<DeptInfo, NoticeHTMLParser> deptInfoNoticeHTMLParserMap;

    public NoticeScraper(Map<DeptInfo, NoticeAPIClient<Document, DeptInfo>> deptInfoNoticeAPIClientMap,
                         Map<DeptInfo, NoticeHTMLParser> deptInfoNoticeHTMLParserMap) {

        this.deptInfoNoticeAPIClientMap = deptInfoNoticeAPIClientMap;
        this.deptInfoNoticeHTMLParserMap = deptInfoNoticeHTMLParserMap;
    }

    @Override
    public List<CommonNoticeFormatDTO> scrap(DeptInfo deptInfo) throws InternalLogicException {
        List<Document> documents;
        List<CommonNoticeFormatDTO> noticeDTOList = new LinkedList<>();

        long start = System.currentTimeMillis();
        log.info("{} HTML 요청", deptInfo.getDeptName());
        documents = deptInfoNoticeAPIClientMap.get(deptInfo).request(deptInfo);
        log.info("{} HTML 수신", deptInfo.getDeptName());
        long end = System.currentTimeMillis();
        log.info("소요된 초 = {}", (end - start) / 1000.0);

        List<String[]> parseResult = new LinkedList<>();
        log.info("{} HTML 파싱 시작", deptInfo.getDeptName());
        for (Document document : documents) {
            parseResult.addAll(deptInfoNoticeHTMLParserMap.get(deptInfo).parse(document));
        }
        log.info("{} HTML 파싱 완료", deptInfo.getDeptName());
        log.info("공지 개수 = {}", parseResult.size());

        // 파싱 결과를 commonNoticeFormatDTO로 변환
        for (String[] oneNoticeInfo : parseResult) {
            noticeDTOList.add(CommonNoticeFormatDTO.builder()
                    .articleId(oneNoticeInfo[0])
                    .postedDate(oneNoticeInfo[1])
                    .subject(oneNoticeInfo[2])
                    .build());
        }

        if(noticeDTOList.size() == 0) {
            throw new InternalLogicException(ErrorCode.NOTICE_SCRAPER_CANNOT_SCRAP);
        }

        return noticeDTOList;
    }
}
