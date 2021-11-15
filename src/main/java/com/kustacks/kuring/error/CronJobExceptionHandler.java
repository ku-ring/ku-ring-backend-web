package com.kustacks.kuring.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CronJobExceptionHandler {

    public void handleInternalLogicException() {
        log.info("InternalLogicException!!!");
    }

    public void handleUnknownException() {
        log.info("UknownException!!!");
    }
}
