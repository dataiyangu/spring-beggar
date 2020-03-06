package com.leesin.spring.formework.webmvc.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/1 16:46
 * @version:
 * @modified By:
 */
@Data
public class HandlerMapping {
    //保存方法对应的实例
    private Object controller;
    //保存映射的方法
    private Method method;
    //URL的正则匹配
    private Pattern pattern;

    public HandlerMapping(Pattern pattern, Object controller, Method method) {
        this.method = method;
        this.pattern = pattern;
        this.controller = controller;
    }
}
