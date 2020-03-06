package com.leesin.spring.formework.webmvc.servlet;

import java.util.Map;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/1 19:12
 * @version:
 * @modified By:
 */
public class ModelAndView {

    private String viewName;
    //参数的key value
    private Map<String, ?> model;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }
//    public void setViewName(String viewName) {
//        this.viewName = viewName;
//    }

    public Map<String, ?> getModel() {
        return model;
    }

//    public void setModel(Map<String, ?> model) {
//        this.model = model;
//    }
}
