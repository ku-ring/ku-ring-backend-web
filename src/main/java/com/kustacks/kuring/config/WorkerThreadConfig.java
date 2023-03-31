package com.kustacks.kuring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class WorkerThreadConfig {

    @Bean
    public ThreadPoolTaskExecutor departmentNoticeUpdaterThreadTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setKeepAliveSeconds(10);
        taskExecutor.setCorePoolSize(6);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setThreadNamePrefix("Depart-thread-");
        taskExecutor.initialize();
        return taskExecutor;
    }
}
