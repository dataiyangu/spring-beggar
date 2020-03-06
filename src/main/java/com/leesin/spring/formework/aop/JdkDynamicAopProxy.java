package com.leesin.spring.formework.aop;

import com.leesin.spring.formework.aop.intercept.MethodInvocation;
import com.leesin.spring.formework.aop.support.AdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/3 14:57
 * @version:
 * @modified By:
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private AdvisedSupport advised;

    public JdkDynamicAopProxy(AdvisedSupport config) {
        this.advised = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //为什么第一个参数是method，执行器链，before method after，所以这里将method作为key记录下，
        //通过这个方法就能拿到这个执行器链的上下文
        //责任链模式
        List<Object> interceptorsAndDynamicInterceptionAdvice = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getTargetClass());
        //methodInvocation 真正去调用方法逻辑的调用器
        MethodInvocation invocation = new MethodInvocation(proxy, this.advised.getTarget(), method, args, this.advised.getTargetClass(), interceptorsAndDynamicInterceptionAdvice);
        //
        return invocation.proceed();
    }
}
