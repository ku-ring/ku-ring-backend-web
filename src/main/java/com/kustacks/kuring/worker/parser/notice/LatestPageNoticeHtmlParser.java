package com.kustacks.kuring.worker.parser.notice;

import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class LatestPageNoticeHtmlParser extends NoticeHtmlParserTemplate {

    @Override
    public boolean support(DeptInfo deptInfo) {
        return true;
    }

    @Override
    protected Elements selectImportantRows(Document document) {
        return document.select(".board-table > tbody > tr").select(".notice");
    }

    @Override
    protected Elements selectNormalRows(Document document) {
        return document.select(".board-table > tbody > tr").not(".notice");
    }

    @Override
    protected String[] extractNoticeFromRow(Element row) {
        Elements tds = row.getElementsByTag("td");

        String title = "";
        String date = "";
        String number = "";

        if(tds.size() > 3){ // td의 사이즈가 다른 학과 사이트에서 가져올 경우 NPE가 발생
            // 기존 로직 (td가 충분한 경우: 기존 로직 먼저 시도)
            try {
                // articleId, postedDate, subject
                String[] splitResults = tds.get(1).select("a").attr("onclick")
                        .replace("jf_viewArtcl('", "")
                        .replace("')", "")
                        .split("', '");

                number = splitResults[2];
                date = tds.get(3).text();
                title = tds.get(1).select("strong").text();

            } catch (Exception e) {
                log.debug("Notice row parsing failed, fallback used : row={}", row.text(), e);
            }
        }

        // td의 사이즈가 다른 경우나 처리 중 오류 발생 시 따로 처리
        if (title.isBlank()) title = extractTitleFallback(row);
        if (date.isBlank() || !isDateLike(date)) date = findDateFallback(row, tds);
        if (number.isBlank()) number = extractNumberFallback(row, tds);

        // 블라인드 게시물이면 null 반환
        if(isBlind(title, number)) return null;


        return new String[]{number, date, title};
    }

    private boolean isBlind(String title, String number) {
        return number.isBlank() && title.contains("블라인드");
    }

    private String extractTitleFallback(Element row) {
        Element strong = row.selectFirst("strong");
        if (strong != null) return strong.text().trim();

        Element a = row.selectFirst("a");
        if (a != null) return a.text().trim();

        return "";
    }

    private String extractNumberFallback(Element row, Elements tds) {
        // onclick만(데이터 무결성 고려)
        Element a = row.selectFirst("a");
        if (a != null) {
            String parsed = parseNumberFromOnclick(a.attr("onclick"));
            if (!parsed.isBlank()) return parsed;
        }

        return "";
    }

    private String parseNumberFromOnclick(String onclick) {
        if (onclick == null || onclick.isBlank()) return "";
        if (!onclick.contains("jf_viewArtcl")) return "";

        String cleaned = onclick
                .replace("jf_viewArtcl('", "")
                .replace("')", "");

        String[] splitResults = cleaned.split("', '");
        if (splitResults.length < 3) return "";

        return splitResults[2].trim();
    }

    private boolean isDateLike(String text) {
        if (text == null) return false;
        String s = text.trim();
        if (s.isBlank()) return false;

        return s.matches("^\\d{4}[-./]\\d{1,2}[-./]\\d{1,2}$");
    }

    private String findDateFallback(Element tr, Elements tds) {
        for (Element td : tds) {
            String d = extractDate(td.text().trim());
            if (!d.isBlank()) return d;
        }
        return extractDate(tr.text());
    }

    private String extractDate(String text) {
        if (text == null) return "";
        Pattern p = Pattern.compile("(\\d{4}[-./]\\d{1,2}[-./]\\d{1,2})");
        Matcher m = p.matcher(text);
        if (m.find()) return m.group(1);
        return "";
    }
}
