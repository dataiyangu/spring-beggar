package com.leesin.spring.formework.aop.intercept;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/28 17:44
 * @version:
 * @modified By: Leesin Dong
 */
public interface MethodInterceptor {
    Object invoke(MethodInvocation invocation) throws Throwable;
}
