package com.leesin.spring.formework.aop;

import com.leesin.spring.formework.aop.support.AdvisedSupport;

/**
 * Created by Tom on 2019/4/14.
 */
public class CglibAopProxy implements  AopProxy {
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
