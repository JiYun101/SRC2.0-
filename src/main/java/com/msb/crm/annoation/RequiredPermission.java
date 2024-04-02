package com.msb.crm.annoation;

import java.lang.annotation.*;

/**
 * 定义方法需要对应资源的权限码
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredPermission {
    String code() default "";
}
