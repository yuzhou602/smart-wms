package com.smartwms.system.log.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogAnnotation {

    String module() default "";

    String operation() default "";
}
