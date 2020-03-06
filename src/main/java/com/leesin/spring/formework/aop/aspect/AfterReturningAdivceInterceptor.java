package com.leesin.spring.formework.aop.aspect;

import com.leesin.spring.formework.aop.intercept.MethodInterceptor;
import com.leesin.spring.formework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/5 20:32
 * @version:
 * @modified By:
 */
public class AfterReturningAdivceInterceptor extends AbstractAspectAdvice implements Advice, MethodInterceptor {

    private JoinPoint joinPoint;

    public AfterReturningAdivceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object retVal = invocation.proceed();
        this.joinPoint = invocation;
        this.afterReturning(retVal, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return retVal;
    }

    protected void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, retVal, null);
    }

}
