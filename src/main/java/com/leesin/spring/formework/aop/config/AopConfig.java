package com.leesin.spring.formework.aop.config;

import lombok.Data;

/**
 * @description:
 * @author: Leesin Dong
 * @date: Created in 2020/3/5 13:50
 * @version:
 * @modified By:
 */
@Data
public class AopConfig {
    private String pointCut;
    private String aspectClass;
    private String aspectAfter;
    private String aspectBefore;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;
}
