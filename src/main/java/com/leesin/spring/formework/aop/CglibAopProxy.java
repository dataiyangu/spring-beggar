package com.leesin.spring.formework.aop;

import com.leesin.spring.formework.aop.support.AdvisedSupport;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/3 14:57
 * @version:
 * @modified By:
 */
public class CglibAopProxy implements AopProxy {
    public CglibAopProxy(AdvisedSupport config) {
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
