package com.leesin.spring.formework.aop.aspect;

import com.leesin.spring.formework.aop.intercept.MethodInterceptor;
import com.leesin.spring.formework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/5 20:30
 * @version:
 * @modified By:
 */
public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice implements Advice, MethodInterceptor {

    private JoinPoint joinPoint;

    public MethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        //传送给织入的参数
        // method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
