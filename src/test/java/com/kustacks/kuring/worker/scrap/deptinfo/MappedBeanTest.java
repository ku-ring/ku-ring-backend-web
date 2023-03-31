package com.kustacks.kuring.worker.scrap.deptinfo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MappedBeanTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void department_name_mapping_to_dept_info_success() {
        // when
        Map deptInfoMap = applicationContext.getBean("departmentNameDeptInfoMap", Map.class);

        // then
        assertThat(deptInfoMap.size()).isEqualTo(62);
    }
}
