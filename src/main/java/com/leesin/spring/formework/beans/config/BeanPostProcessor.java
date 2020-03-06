package com.leesin.spring.formework.beans.config;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/1 14:09
 * @version:
 * @modified By:
 */
public class BeanPostProcessor {
    public Object postProcessBeforeInitiaLization(Object bean, String beanName) throws Exception {
        return bean;
    }

    public Object postProcessAfterInitiaLization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
