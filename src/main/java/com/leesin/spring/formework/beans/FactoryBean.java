package com.leesin.spring.formework.beans;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/2/29 22:09
 * @version:
 * @modified By:
 */
public interface FactoryBean {
    Object getBean(String beanName);
}
