package com.leesin.spring.formework.beans.support;

import com.leesin.spring.formework.beans.config.BeanDefinition;
import com.leesin.spring.formework.context.support.AbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/27 22:21
 * @version:
 * @modified By: Leesin Dong
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {
    //存储注册信息的BeanDefinition
    protected final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String,BeanDefinition>();
}
