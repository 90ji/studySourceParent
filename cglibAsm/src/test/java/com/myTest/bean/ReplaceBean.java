package com.myTest.bean;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Method;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public class ReplaceBean implements MethodReplacer{
    public String doSomething(Integer integer){
        return (integer+100)+"";
    }

    @Override
    public Object reimplement(Object obj, Method method, Object[] args) throws Throwable {

        System.out.println("改变了原有的实现逻辑");
        return doSomething((Integer) args[0]);
    }
}
