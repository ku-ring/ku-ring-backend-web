package com.kustacks.kuring.ai.adapter.out.persistence;

import com.kustacks.kuring.notice.domain.CategoryName;
import com.kustacks.kuring.worker.parser.notice.PageTextDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.ChromaVectorStore;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChromaVectorStoreAdapterTest {

    @Mock
    ChromaVectorStore chromaVectorStore;

    @DisplayName("embedding이 성공하는지 확인")
    @Test
    void embedding() {
        // given
        ChromaVectorStoreAdapter vectorStoreAdapter = new ChromaVectorStoreAdapter(chromaVectorStore);

        String text = "건국대학교 산학협력단 2024년 하반기 KOICA ODA 영프로페셔널(YP) 채용 공고 건국대학교 「산학협력단」은 연구자가 최고의 성과를 " +
                "도출할 수 있는 연구 환경을 조성하고, 연구기획·지원에서 지식재산 관리, 대외 이해관계자와의 협력은 물론, 교내 우수 연구자 및 기술을 " +
                "기반으로 건국대 기술지주회사 자회사를 통한 기술사업화 고도화 지원을 위해 2004년 3월 출범하였습니다. 국제개발 협력과 사회공헌 분야에 " +
                "관심 있는 인재를 다음과 같이 채용하고자 합니다. 2024년 6월 4일 건국대학교 산학협력단장 1. 채용 분야 및 인원 채용 분야 채용 인원 " +
                "근무지 담당업무 KOICA ODA YP 1명 건대·코이카·베트남 국립농업대학교 축산고등교육센터(KUVEC) ODA 및 국제개발 협력 사업수행 실무 " +
                "사업수행 과정 모니터링 및 대외협력 업무 등 2. 지원 자격 구분 필수 자격 사항 우대사항 공통 사항 1. 국가공무원법 제33조 각 호에 " +
                "해당하는 결격사유가 없는 자 2. 공무원임용시험령 등 관계 법령에 의하여 응시 자격이 정지되지 아니한 자 3. 남자는 병역필 또는 면제자 " +
                "4. 해외여행에 결격사유가 없는 자 5. 공무원 채용 신체검사 기준에 결격사유가 없는 자 6. 만 19세 이상 만 34세 이하 대한민국 국적을 " +
                "가진 미취업자 (단, 군필자는 해당 법률에 따라 연령 연장) 7. KOICA 개발 협력 사업수행기관 YP로 근무한 경험이 없는 자 1. " +
                "사회배려층 우대 - 장애인, 저소득층, 차상위계층, 국가보훈대상자, 지방인재, 북한이탈주민, 여성 가장, 결혼이주자, 다문화가정, " +
                "위탁가정 및 아동 보육 시설 재원자(보호 종료 아동)(※ 공고 시작일 기준 등록된 자) 2. 국제 개발협력 관련 활동 경험자 및 전공자 " +
                "3. 영어 능통자 우대 4. 한글, 워드, 엑셀, 파워포인트 등 프로그램 활용 가능자 우대 5. KOICA ODA 자격증 보유자 6. 대학교 행정업무" +
                " 경험자 우대 ※ 군필자 응시 연령 상한: 「제대군인지원에 관한 법률」 제16조 1항에 의거 제대군인에 대한 채용시험 응시 연령 상한을 다음 " +
                "각 호와 같이 연장함 ▷ 2년 이상의 복무기간을 마치고 전역한 제대군인: 만 37세 ▷ 1년 이상 2년 미만의 복무기간을 마치고 전역한 " +
                "제대군인: 만 36세 ▷ 1년 미만의 복무기간을 마치고 전역한 제대군인: 만 35세 ※ 장애인 인정 범위: 「장애인복지법」상 장애인 기준에 " +
                "해당하는 자 또는「국가유공자 등 예우 및 지원에 관한 법률」상 상이등급 기준에 해당하는 자 ※ 저소득층의 범위: 「국민기초생활보장법」 " +
                "제2조제2호·제11호의 규정에 의한 기초생활보장 수급자 및 차상위계층에 속한 자와 「한부모가족지원법」 제5조의 규정에 의하여 보호를 " +
                "받는 한부모가족 세대주 ※ 장애인, 저소득층은 각 단계별 전형의 만점 기준 4% 가점 부여 ※ 국가유공자는「국가유공자 등 예우 및 " +
                "지원에 관한 법률」에 따른 해당 가점 부여 ※ KOICA 영프로페셔널 사업 개발협력 사업수행기관 YP 기참여자 재지원 불가능, KOICA " +
                "해외사무소/재외공관 YP 기 참여자 지원 가능 3. 채용 기간 및 근무 조건 가. 계약기간: 2024. 8. 1. ~ 2025. 2. 28.(7개월) " +
                "(연장 없음) 나. 근무 조건: 전일제 (주 5일 09:00 ~ 18:00, 주당 40시간 근무) 다. 보수액: 고용노동부 고시 최저임금 적용 예정" +
                "(세전 약 207만 원, 2024년 최저임금 9,860원/시간) ※ 2025년 1, 2월분은 최저임금 인상에 따라 급여 변동 예정 라. 근무 장소: " +
                "건국대학교 서울캠퍼스(상허생명과학대학 KUVEC 710-1호) 4. 채용 절차 가. 1차 전형: 서류심사 나. 2차 전형: 면접 심사 " +
                "※ 서류전형 합격자는 채용 인원의 5배수 이내로 하며, 면접 일정·장소 등은 서류전형 합격자에 한해 개별 통보 5. 제출 서류 가. " +
                "원서 접수 기간에 제출하는 서류 ◦ 첨부된 양식의 입사지원서 1부 ◦ 첨부된 양식의 개인정보 수집 및 이용 동의서 1부 ";

        PageTextDto pageTextDto = new PageTextDto("건국대학교 산학협력단 2024년 하반기 KOICA ODA 영프로페셔널(YP) 채용 공고", "articeId", "2024.03.11", text);

        // when
        vectorStoreAdapter.embedding(List.of(pageTextDto), CategoryName.INDUSTRY_UNIVERSITY);

        // then
        verify(chromaVectorStore, times(1)).accept(anyList());
    }
}
