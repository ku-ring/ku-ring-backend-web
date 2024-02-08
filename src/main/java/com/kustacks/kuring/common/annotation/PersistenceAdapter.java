package com.kustacks.kuring.common.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repository
public @interface PersistenceAdapter {

  @AliasFor(annotation = Repository.class)
  String value() default "";
}
