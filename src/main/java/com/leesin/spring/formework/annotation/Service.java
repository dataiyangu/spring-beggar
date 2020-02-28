package com.leesin.spring.formework.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/27 20:03
 * @version:
 * @modified By:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
