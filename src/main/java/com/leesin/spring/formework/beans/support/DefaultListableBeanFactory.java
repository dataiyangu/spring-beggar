package com.leesin.spring.formework.beans.support;

import com.leesin.spring.formework.beans.config.BeanDefinition;
import com.leesin.spring.formework.context.support.AbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/2/29 22:36
 * @version:
 * @modified By:
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {
    //存储注册信息的BeanDefinition
    //spring的IOC容器（伪IOC容器，真正的IOC容器是保存BeanWrapper的容器）
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
}
