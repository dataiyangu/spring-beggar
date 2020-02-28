package com.leesin.spring.formework.aop.config;

import lombok.Data;

/**
 * @description:
 * @author: Administrator
 * @date: Created in 2020/2/28 11:48
 * @version:
 * @modified By: Leesin Dong
 */
@Data
public class AopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
