package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.domain.AcademicEventCategory;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.kustacks.kuring.calendar.domain.AcademicEventCategory.ACADEMIC_DEGREE;
import static com.kustacks.kuring.calendar.domain.AcademicEventCategory.ACADEMIC_OPERATION_EVENT;
import static com.kustacks.kuring.calendar.domain.AcademicEventCategory.REGISTRATION_COURSE_GRADE;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AcademicEventCategorizer {

    private static final Map<AcademicEventCategory, List<String>> CATEGORY_KEYWORDS = Map.of(
            ACADEMIC_DEGREE, List.of(
                    "휴학", "복학", "재입학", "졸업유예", "조기졸업", "졸업논문", "논문입력", "예비사정",
                    "전과", "다부전공", "교직다전공", "교직이수", "교직무시험", "검정원서", "교직이수예정자"
            ),
            REGISTRATION_COURSE_GRADE, List.of(
                    "수강정정", "초과과목", "수강신청", "바구니", "자동신청", "시간표조회", "수강신청확인서",
                    "등록", "등록개시", "등록금", "납입", "반환", "취득학점포기", "성적확인", "성적이의신청",
                    "최종성적", "성적등재", "성적조회", "성적표", "장학", "학비감면", "서류제출",
                    "폐강교과목", "폐강대상자", "환불", "강의시간표"
            ),
            ACADEMIC_OPERATION_EVENT, List.of(
                    "제적대상자", "제적처리", "재적생변동", "중간고사", "기말고사", "성적입력오픈",
                    "개강", "방학", "하계", "동계", "계절학기", "종강", "공휴일", "추석", "한글날", "성탄절", "신정", "삼일절",
                    "학위수여식", "졸업대상자선정", "졸업사정회", "졸업확정처리", "워크숍",
                    "예술제", "축전", "일감호", "학원창립", "개교기념일", "기념일"
            )
    );

    public static AcademicEventCategory categorize(String summary) {
        if (summary == null || summary.isBlank()) {
            return AcademicEventCategory.ETC;
        }

        String normalizedSummary = summary.trim();

        if (containsKeywords(normalizedSummary, CATEGORY_KEYWORDS.get(ACADEMIC_DEGREE))) {
            return ACADEMIC_DEGREE;
        }

        if (containsKeywords(normalizedSummary, CATEGORY_KEYWORDS.get(REGISTRATION_COURSE_GRADE))) {
            return REGISTRATION_COURSE_GRADE;
        }

        if (containsKeywords(normalizedSummary, CATEGORY_KEYWORDS.get(ACADEMIC_OPERATION_EVENT))) {
            return ACADEMIC_OPERATION_EVENT;
        }

        return AcademicEventCategory.ETC;
    }

    private static boolean containsKeywords(String text, List<String> keywords) {
        return keywords.stream()
                .anyMatch(text::contains);
    }
}