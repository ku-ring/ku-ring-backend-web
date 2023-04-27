package com.kustacks.kuring.worker.scrap.deptinfo;

import com.kustacks.kuring.notice.domain.DepartmentName;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RegisterDepartmentMap {
    DepartmentName key();
}
