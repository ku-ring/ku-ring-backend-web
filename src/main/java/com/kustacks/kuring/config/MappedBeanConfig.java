package com.kustacks.kuring.config;

import com.kustacks.kuring.worker.CategoryName;
import com.kustacks.kuring.worker.DepartmentName;
import com.kustacks.kuring.worker.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MappedBeanConfig {

    @Configuration
    public static class APINoticeMappedBeanConfig {

        private final NoticeApiClient kuisNoticeApiClient;
        private final NoticeApiClient libraryNoticeApiClient;

        public APINoticeMappedBeanConfig(NoticeApiClient kuisNoticeApiClient, NoticeApiClient libraryNoticeApiClient) {
            this.kuisNoticeApiClient = kuisNoticeApiClient;
            this.libraryNoticeApiClient = libraryNoticeApiClient;
        }

        @Bean
        public Map<CategoryName, NoticeApiClient> noticeApiClientMap() {
            HashMap<CategoryName, NoticeApiClient> map = new HashMap<>();

            for (CategoryName categoryName : CategoryName.values()) {
                if (categoryName.equals(CategoryName.LIBRARY)) {
                    map.put(categoryName, libraryNoticeApiClient);
                } else {
                    map.put(categoryName, kuisNoticeApiClient);
                }
            }

            return map;
        }
    }

    @Configuration
    @RequiredArgsConstructor
    public static class DepartmentNameDeptInfoMappedBeanConfig {

        private final ApplicationContext applicationContext;

        @Bean
        public Map<DepartmentName, DeptInfo> departmentNameDeptInfoMap() {
            Map<DepartmentName, DeptInfo> map = new HashMap<>();

            Map<String, DeptInfo> beansOfType = applicationContext.getBeansOfType(DeptInfo.class);
            for (DeptInfo deptInfo : beansOfType.values()) {
                DepartmentName departmentName = deptInfo.getClass().getDeclaredAnnotation(RegisterDepartmentMap.class).key();
                map.put(departmentName, deptInfo);
            }

            return map;
        }
    }
}
