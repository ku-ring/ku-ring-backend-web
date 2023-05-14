package com.kustacks.kuring.config;

import com.kustacks.kuring.category.domain.Category;
import com.kustacks.kuring.category.domain.CategoryRepository;
import com.kustacks.kuring.category.domain.CategoryName;
import com.kustacks.kuring.notice.domain.DepartmentName;
import com.kustacks.kuring.worker.scrap.client.notice.NoticeApiClient;
import com.kustacks.kuring.worker.scrap.deptinfo.DeptInfo;
import com.kustacks.kuring.worker.scrap.deptinfo.RegisterDepartmentMap;
import com.kustacks.kuring.worker.update.notice.dto.response.CommonNoticeFormatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MappedBeanConfig {

    @Configuration
    @RequiredArgsConstructor
    public static class APINoticeMappedBeanConfig {

        private final NoticeApiClient<CommonNoticeFormatDto, CategoryName> kuisNoticeApiClient;
        private final NoticeApiClient<CommonNoticeFormatDto, CategoryName> libraryNoticeApiClient;

        @Bean
        public Map<CategoryName, NoticeApiClient<CommonNoticeFormatDto, CategoryName>> noticeApiClientMap() {
            HashMap<CategoryName, NoticeApiClient<CommonNoticeFormatDto, CategoryName>> map = new HashMap<>();

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
    public static class CategoryEntityNameMappedBeanConfig {

        private final CategoryRepository categoryRepository;

        @Bean
        public Map<String, Category> categoryMap() {
            return categoryRepository.findAllMap();
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
