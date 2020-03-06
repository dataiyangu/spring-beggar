package com.leesin.spring.formework.aop.aspect;

import com.leesin.spring.formework.aop.intercept.MethodInterceptor;
import com.leesin.spring.formework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/5 20:33
 * @version:
 * @modified By:
 */
public class AfterThrowingAdviceInterceptor extends AbstractAspectAdvice implements Advice, MethodInterceptor {

    private String throwingName;

    public AfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        try {
            return invocation.proceed();
        } catch (Throwable throwable) {
            invokeAdviceMethod(invocation, null, throwable.getCause());
            throw throwable;
        }
    }

    public void setThrowName(String throwName) {
        this.throwingName = throwName;
    }
}
