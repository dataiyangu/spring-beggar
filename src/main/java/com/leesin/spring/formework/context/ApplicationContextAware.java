package com.leesin.spring.formework.context;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/2/29 22:55
 * @version:
 * @modified By:
 */

/**
 * 通过解耦方式获得IOC容器的顶层设计
 * 后面将通过一个监听器去扫描所有的类，只要实现了此接口，
 * 将自动调用setApplicationContext()方法，从而将IOC容器注入到目标类中
 * <p>
 * 观察者模式
 */
public interface ApplicationContextAware {
    void setApplicationContext(ApplicationContext applicationContext);
}
