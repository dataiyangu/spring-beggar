package com.leesin.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/6 7:52
 * @version:
 * @modified By:
 */
public interface JoinPoint {
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);
    Object getUserAttribute(String key);
}
