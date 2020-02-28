package com.leesin.spring.formework.aop.support;
import	java.util.HashMap;

import com.leesin.spring.formework.aop.aspect.AfterReturningAdviceInterceptor;
import com.leesin.spring.formework.aop.aspect.AfterThrowingAdviceInterceptor;
import com.leesin.spring.formework.aop.aspect.MethodBeforeAdviceInterceptor;
import com.leesin.spring.formework.aop.config.AopConfig;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/28 11:45
 * @version:
 * @modified By: Leesin Dong
 */
public class AdvisedSupport {
    private Class<?> targetClass;
    private Object target;
    private AopConfig config;
    private Pattern pointCutClassPattern;
    private transient Map<Method,List<Object>> methodCache;
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
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");

        String pointCutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));

        methodCache = new HashMap<Method,List<Object>> ();
        Pattern pattern = Pattern.compile(pointCut);
        try {
            Class<?> aspectClass = Class.forName(this.config.getAspectClass());
            HashMap<String, Method> aspectMethods = new HashMap<>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(), m);
            }
            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                if (methodString.contains("throws")) {
                    methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if (matcher.matches()) {
                    //执行器链
                    List<Object> advices = new LinkedList<Object>();
                    //把每个方法包装成MethodInterceptor
                    //before
                    if (!(null == config.getAspectBefore()) || "".equals(config.getAspectBefore())) {
                        //创建一个Advice
                        advices.add(new MethodBeforeAdviceInterceptor(aspectMethods.get(config.getAspectBefore()), aspectClass.newInstance()));
                    }
                    //after
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))) {
                        //创建一个Advivce
                        advices.add(new AfterReturningAdviceInterceptor(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }
                    //afterThrowing
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))) {
                        //创建一个Advivce
                        AfterThrowingAdviceInterceptor throwingAdvice =
                                new AfterThrowingAdviceInterceptor(
                                        aspectMethods.get(config.getAspectAfterThrow()),
                                        aspectClass.newInstance());
                        throwingAdvice.setThrowName(config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
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
