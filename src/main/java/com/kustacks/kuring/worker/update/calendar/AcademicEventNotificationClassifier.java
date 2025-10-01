package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.calendar.domain.Transparent;

import java.util.List;
import java.util.Objects;

public class AcademicEventNotificationClassifier {

    // 학생이 행정적 액션을 해야하는 일정들 (알림 필수 키워드)
    private static final List<String> ACTION_REQUIRED_KEYWORDS = List.of(
            // 수강 관련
            "수강정정", "초과과목신청", "수강신청", "바구니", "자동신청", "시간표조회", "수강신청확인서",

            // 등록 관련  
            "등록", "등록개시", "등록마감", "등록금", "납입", "반환", "환불마감",

            // 학적 관련
            "휴학신청", "복학신청", "재입학접수", "졸업유예접수", "조기졸업", "졸업논문입력", "예비사정",
            "전과신청", "다부전공신청", "다부전공포기", "교직다전공", "교직이수", "교직무시험", "검정원서접수",

            // 성적 관련
            "성적확인", "성적이의신청", "최종성적이의신청", "성적등재", "성적조회", "성적증명서", "성적표", "취득학점포기",

            // 장학 관련
            "장학신청", "학비감면", "서류제출",

            // 폐강 관련
            "폐강교과목공지", "폐강대상자",

            // 일반 액션 키워드
            "신청", "접수", "제출", "마감", "포기", "확인", "조회", "출력"
    );

    public static boolean proceed(Transparent transparent, String summary) {
        return isNotifiableByTransparent(transparent) || isNotifiableBySummary(summary);
    }

    /**
     * Transparent 속성으로 알림 필요 여부 판단
     * OPAQUE = 중요일정 → 알림 발송
     * TRANSPARENT = 미중요일정 → 알림 미발송
     */
    private static boolean isNotifiableByTransparent(Transparent transparent) {
        if (Objects.isNull(transparent)) {
            return false;
        }

        return switch (transparent) {
            case OPAQUE -> true;
            case TRANSPARENT -> false;
        };
    }

    /**
     * summary 내용으로 알림 필요 여부 판단
     */
    private static boolean isNotifiableBySummary(String summary) {
        if (summary == null || summary.isBlank()) {
            return false;
        }

        String normalizedSummary = summary.trim();
        return containsActionRequiredKeywords(normalizedSummary);
    }

    private static boolean containsActionRequiredKeywords(String text) {
        return ACTION_REQUIRED_KEYWORDS.stream()
                .anyMatch(text::contains);
    }
}