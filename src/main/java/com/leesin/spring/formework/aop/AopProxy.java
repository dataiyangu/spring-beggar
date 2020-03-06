package com.leesin.spring.formework.aop;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/3 14:56
 * @version:
 * @modified By:
 */
public interface AopProxy {
    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
