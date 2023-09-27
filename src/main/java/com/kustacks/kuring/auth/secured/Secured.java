package com.kustacks.kuring.auth.secured;

import com.kustacks.kuring.admin.domain.AdminRole;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Secured {
    AdminRole[] value();
}
