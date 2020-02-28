package com.leesin.spring.formework.beans.config;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/27 22:07
 * @version:
 * @modified By: Leesin Dong
 */
//用来存储配置文件中的信息
//相当于保存在内存中的配置
public class BeanDefinition {
    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;

    public BeanDefinition() {

    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
