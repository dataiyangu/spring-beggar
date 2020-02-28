package com.leesin.spring.formework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/28 20:04
 * @version:
 * @modified By: Leesin Dong
 */
public interface JoinPoint {
    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
