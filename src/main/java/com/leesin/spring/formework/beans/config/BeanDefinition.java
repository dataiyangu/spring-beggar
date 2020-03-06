package com.leesin.spring.formework.beans.config;

import lombok.Data;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/2/29 22:46
 * @version:
 * @modified By:
 */
@Data
public class BeanDefinition {
    private String beanClassName;
    private boolean lazyInit = false;
    //在factory中存储的名字是什么？
    private String factoryBeanName;
    private boolean isSingleton = true;
}
