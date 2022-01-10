package com.kustacks.kuring.config;

import com.kustacks.kuring.kuapi.CategoryName;
import com.kustacks.kuring.kuapi.api.notice.NoticeAPIClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MappedBeanConfig {

    private final NoticeAPIClient kuisNoticeAPIClient;
    private final NoticeAPIClient libraryNoticeAPIClient;

    public MappedBeanConfig(NoticeAPIClient kuisNoticeAPIClient, NoticeAPIClient libraryNoticeAPIClient) {
        this.kuisNoticeAPIClient = kuisNoticeAPIClient;
        this.libraryNoticeAPIClient = libraryNoticeAPIClient;
    }

    @Bean
    public Map<CategoryName, NoticeAPIClient> noticeAPIClientMap() {
        HashMap<CategoryName, NoticeAPIClient> map = new HashMap<>();
        for (CategoryName categoryName : CategoryName.values()) {
            if(categoryName.equals(CategoryName.LIBRARY)) {
                map.put(categoryName, libraryNoticeAPIClient);
            } else {
                map.put(categoryName, kuisNoticeAPIClient);
            }
        }
        return map;
    }
}
