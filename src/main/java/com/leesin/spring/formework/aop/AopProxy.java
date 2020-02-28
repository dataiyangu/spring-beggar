package com.leesin.spring.formework.aop;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/28 19:06
 * @version:
 * @modified By: Leesin Dong
 */
public interface AopProxy {
    Object getProxy();
    Object getProxy(ClassLoader classLoader);
}
