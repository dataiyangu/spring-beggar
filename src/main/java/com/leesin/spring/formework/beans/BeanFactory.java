package com.leesin.spring.formework.beans;

/**
 * @description:单例工厂的顶层设计
 * @author: Leesin Dong
 * @date: Created in 2020/2/29 22:08
 * @version:
 * @modified By:
 */
public interface BeanFactory {
    Object getBean(String beanName) throws Exception;
}
