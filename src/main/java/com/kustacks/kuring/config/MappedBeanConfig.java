package com.kustacks.kuring.config;


import com.kustacks.kuring.notice.domain.DepartmentName;
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
