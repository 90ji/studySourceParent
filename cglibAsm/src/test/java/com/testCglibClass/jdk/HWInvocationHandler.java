package com.testCglibClass.jdk;

/**
 * Create by qxz on 2018/3/2
 * Description:
 */
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HWInvocationHandler implements InvocationHandler {
    //目标对象
    private Object target;

    public HWInvocationHandler(Object target){
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("------插入前置通知代码-------------");
        //执行相应的目标方法
        Object rs = method.invoke(target,args);
        System.out.println("------插入后置处理代码-------------");
        return rs;
    }

}