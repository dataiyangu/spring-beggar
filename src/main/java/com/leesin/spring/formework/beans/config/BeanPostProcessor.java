package com.leesin.spring.formework.beans.config;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/28 11:20
 * @version:
 * @modified By: Leesin Dong
 */
public class BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean,String beanName) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean,String beanName) {
        return bean;
    }
}
