package com.kustacks.kuring.config;

import com.kustacks.kuring.worker.CategoryName;
import com.kustacks.kuring.worker.client.notice.NoticeApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class MappedBeanConfig {

    private final NoticeApiClient kuisNoticeApiClient;

    private final NoticeApiClient libraryNoticeApiClient;

    @Bean
    public Map<CategoryName, NoticeApiClient> noticeApiClientMap() {
        HashMap<CategoryName, NoticeApiClient> map = new HashMap<>();

        for (CategoryName categoryName : CategoryName.values()) {
            if(categoryName.equals(CategoryName.LIBRARY)) {
                map.put(categoryName, libraryNoticeApiClient);
            } else {
                map.put(categoryName, kuisNoticeApiClient);
            }
        }

        return map;
    }
}
