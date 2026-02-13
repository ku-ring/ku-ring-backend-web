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

    /**
     * Extracts the notice identifier, posted date, and title from a table row element.
     *
     * <p>Parses the row defensively and applies fallback strategies when expected cells are missing
     * or values cannot be parsed.</p>
     *
     * @param row the table row Element representing a single notice entry
     * @return a String array in the order {@code [number, date, title]}, or {@code null} if the row
     *         represents a blind notice
     */
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

    /**
     * Determines whether a notice row is a blind notice.
     *
     * @param title  the notice title text
     * @param number the notice number string (may be blank)
     * @return `true` if `number` is blank and `title` contains "블라인드", `false` otherwise
     */
    private boolean isBlind(String title, String number) {
        return number.isBlank() && title.contains("블라인드");
    }

    /**
     * Extracts a fallback title from the given HTML row element.
     *
     * Attempts to return the trimmed text of a first-level <strong> element, or if absent,
     * the trimmed text of the first <a> element. Returns an empty string if no title is found.
     *
     * @param row the table row element to extract the title from
     * @return the extracted title text, or an empty string if none is found
     */
    private String extractTitleFallback(Element row) {
        Element strong = row.selectFirst("strong");
        if (strong != null) return strong.text().trim();

        Element a = row.selectFirst("a");
        if (a != null) return a.text().trim();

        return "";
    }

    /**
     * Extracts a notice number from the row's first anchor element as a fallback.
     *
     * @param row the HTML table row element to inspect for an anchor with an `onclick` attribute
     * @param tds the row's table cell elements (not used by this fallback but provided by caller)
     * @return the parsed notice number, or an empty string if no number could be extracted
     */
    private String extractNumberFallback(Element row, Elements tds) {
        // onclick만(데이터 무결성 고려)
        Element a = row.selectFirst("a");
        if (a != null) {
            String parsed = parseNumberFromOnclick(a.attr("onclick"));
            if (!parsed.isBlank()) return parsed;
        }

        return "";
    }

    /**
     * Extracts the third argument (notice identifier) from a `jf_viewArtcl('...','...','...')` onclick string.
     *
     * @param onclick the raw onclick attribute value from an anchor element; may be null or blank
     * @return the third argument from the `jf_viewArtcl` call trimmed, or an empty string if the input is null, blank,
     *         not a `jf_viewArtcl` call, or does not contain at least three arguments
     */
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

    /**
     * Determines whether the given text is exactly a date in the form yyyy[-./]M{1,2}[-./]d{1,2}.
     *
     * @param text the string to test for a date pattern
     * @return `true` if the input matches a four-digit year followed by a month and day separated by '-', '.', or '/', `false` otherwise
     */
    private boolean isDateLike(String text) {
        if (text == null) return false;
        String s = text.trim();
        if (s.isBlank()) return false;

        return s.matches("^\\d{4}[-./]\\d{1,2}[-./]\\d{1,2}$");
    }

    /**
     * Locate a date substring within a table row by checking each cell in order, then the whole row.
     *
     * @param tr  the table row element to use as a final fallback when no cell contains a date
     * @param tds the collection of table cell elements to inspect for a date, searched in iteration order
     * @return the first date string found (matching yyyy[-./]m{1,2}[-./]d{1,2}), or an empty string if none is found
     */
    private String findDateFallback(Element tr, Elements tds) {
        for (Element td : tds) {
            String d = extractDate(td.text().trim());
            if (!d.isBlank()) return d;
        }
        return extractDate(tr.text());
    }

    /**
     * Extracts the first substring that matches a date in the form yyyy[-./]M{1,2}[-./]d{1,2} from the given text.
     *
     * @param text the input text to search; may be null
     * @return the first matching date substring (for example "2023-05-01" or "2023/5/1"), or an empty string if no date is found or the input is null
     */
    private String extractDate(String text) {
        if (text == null) return "";
        Pattern p = Pattern.compile("(\\d{4}[-./]\\d{1,2}[-./]\\d{1,2})");
        Matcher m = p.matcher(text);
        if (m.find()) return m.group(1);
        return "";
    }
}