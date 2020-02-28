package com.leesin.spring.formework.core;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/27 21:59
 * @version:
 * @modified By:Leesin Dong
 */
/*单例工厂的顶层设计*/
public interface BeanFactory {
    Object GetBean(String Name) throws Exception;
}
