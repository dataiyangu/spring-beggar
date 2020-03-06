package com.leesin.spring.formework.aop.support;

import com.leesin.spring.formework.aop.aspect.AfterReturningAdivceInterceptor;
import com.leesin.spring.formework.aop.aspect.AfterThrowingAdviceInterceptor;
import com.leesin.spring.formework.aop.aspect.MethodBeforeAdviceInterceptor;
import com.leesin.spring.formework.aop.config.AopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/3 15:01
 * @version:
 * @modified By:
 */
public class AdvisedSupport {
    private Class<?> targetClass;

    private Object target;

    private AopConfig config;

    //正则
    private Pattern pointCutClassPattern;

    private transient Map<Method, List<Object>> methodCache;

    public AdvisedSupport(AopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public Object getTarget() {
        return this.target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if (cached == null) {
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            //底层逻辑，对代理方法进行一个兼容处理
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        //这里的意思\\.就是配置中的.为了获取他，\\\\.就是\.的意思，是为了将我们配置中普通的转化为真正的正则。进行匹配。
        String pointCut = config.getPointCut().replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        //（）里面是方法，得到方法往前的4个，也就是class的位置
        String pointCutForClassRegix = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        // pointCut=public .* com.leesin.spring.demo.service..*Service..*(.*)
        // 这里取得的是 class com.leesin.spring.demo.service..*Service 这段，其他的先不管。
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegix.substring(
                pointCutForClassRegix.lastIndexOf(" ") + 1));
        Pattern pattern = Pattern.compile(pointCut);

        methodCache = new HashMap<Method, List<Object>>();

        try {
            Map<String, Method> aspectMethods = new HashMap<String, Method>();
            Class<?> aspectClass = Class.forName(this.config.getAspectClass());
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }


            for (Method method : this.getTargetClass().getMethods()) {
                String methodString = method.toString();
                if (methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    //执行器链
                    LinkedList<Object> advices = new LinkedList<Object>();
                    //把每个方法包装秤MethodIntercepter
                    //before
                    if (!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))) {
                        // 创建一个Advice
                        // advices.add()
                        advices.add(new MethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }
                    //after
                    if (!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        // 创建一个Advice
                        // advices.add()
                        advices.add(new AfterReturningAdivceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }
                    //afterThrowing
                    if (!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        // 创建一个Advice
                        // advices.add()
                        AfterThrowingAdviceInterceptor throwingAdvice =
                                new AfterThrowingAdviceInterceptor(
                                        aspectMethods.get(config.getAspectAfterThrow()),
                                        aspectClass.newInstance());
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(method, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
