package com.myTest.callbackStrategy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public class MyCallbcakInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("pre\n");
        Object invoke = proxy.invokeSuper(obj, args);
        System.out.println("after\n");
        return invoke;
    }
}
