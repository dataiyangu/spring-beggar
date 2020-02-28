package com.leesin.spring.formework.beans;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/27 22:15
 * @version:
 * @modified By: Leesin Dong
 */
public class BeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public BeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    //返回代理以后的class
    //可能会使这个$Proxy0
    public Class<?> getWrappedClass() {
        return wrappedClass;
    }
}
