package com.kustacks.kuring.worker.scrap.deptinfo;

import com.kustacks.kuring.config.MappedBeanConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class MappedBeanTest {
    @Test
    public void department_name_mapping_to_dept_info_success() {
        // given
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MappedBeanConfig.DepartmentNameDeptInfoMappedBeanConfig.class);

        // when
        Map deptInfoMap = applicationContext.getBean("departmentNameDeptInfoMap", Map.class);

        // then
        assertThat(deptInfoMap.size()).isEqualTo(63);
    }
}
