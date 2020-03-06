package com.leesin.spring.test;

import com.leesin.spring.formework.context.ApplicationContext;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/1 12:22
 * @version:
 * @modified By:
 */
public class Test {
    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ApplicationContext("classpath:application.properties");
        Object test = applicationContext.getBean("test");
        System.out.println(applicationContext);
        System.out.println(test);
    }
}
