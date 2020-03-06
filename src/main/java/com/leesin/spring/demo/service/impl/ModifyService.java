package com.leesin.spring.demo.service.impl;

import com.leesin.spring.demo.service.IModifyService;
import com.leesin.spring.formework.annotation.Service;

/**
 * 增删改业务
 *
 * @author Tom
 */
@Service
public class ModifyService implements IModifyService {

    /**
     * 增加
     */
    public String add(String name, String addr) throws Exception {
        Exception exception = new Exception("这是故意抛的异常！！");
        exception.initCause(new Exception("这是故意抛的异常哦，没有理由哦！！"));
        throw exception;
    }

    /**
     * 修改
     */
    public String edit(Integer id, String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    /**
     * 删除
     */
    public String remove(Integer id) {
        return "modifyService id=" + id;
    }

}
