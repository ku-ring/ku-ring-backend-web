package com.kustacks.kuring.config;

import com.kustacks.kuring.error.CronJobExceptionHandler;
import com.kustacks.kuring.error.InternalLogicException;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class KuTaskSchedulerConfig implements TaskSchedulerCustomizer {

    private final CronJobExceptionHandler exceptionHandler;

    public KuTaskSchedulerConfig(CronJobExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void customize(ThreadPoolTaskScheduler taskScheduler) {
        taskScheduler.setErrorHandler(t -> {
            if(t instanceof InternalLogicException) {
                exceptionHandler.handleInternalLogicException();
            } else {
                exceptionHandler.handleUnknownException();
            }
        });
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }
}
