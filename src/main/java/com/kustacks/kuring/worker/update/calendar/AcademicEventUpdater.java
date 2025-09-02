package com.kustacks.kuring.worker.update.calendar;

import com.kustacks.kuring.worker.parser.calendar.IcsParser;
import com.kustacks.kuring.worker.parser.calendar.dto.IcsCalendarResult;
import com.kustacks.kuring.worker.scrap.calendar.IcsScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcademicEventUpdater {

    private final IcsScraper icsScraper;
    private final IcsParser icsParser;
    private final AcademicEventDbSynchronizer academicEventDbSynchronizer;

    //매월 1일 00시 00분 업데이트 진행
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
    public void update() {
        log.info("******** 학사일정 업데이트 시작 ********");
        try {
            Calendar iCalendar = icsScraper.scrapAcademicCalendar();
            IcsCalendarResult result = icsParser.parse(iCalendar);
            academicEventDbSynchronizer.compareAndUpdateDb(result);
        } catch (IOException e) {
            log.error("학교 Outlook 캘린더 연결에 실패하였습니다.");
        } catch (ParserException e) {
            log.error("ICS 파싱에 실패하였습니다.");
        }
        log.info("******** 학사일정 업데이트 완료 ********");
    }
}
