package com.leesin.spring.formework.aop.intercept;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/4 9:27
 * @version:
 * @modified By:
 */
public interface MethodInterceptor {
    //顶层设计，每一个拦截器都会去执行invoke方法，就是去执行拦截器本身的逻辑，
    //说白了就是执行切面里面织入的代码
    Object invoke(MethodInvocation invocation) throws Throwable;
}
