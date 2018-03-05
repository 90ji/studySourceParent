package com.testCglibClass.jdk;

/**
 * Create by qxz on 2018/3/2
 * Description:
 */

import java.lang.reflect.Proxy;

public class JdkDymamicPoxy {
    public static void main(String[] args) {
        //生成$Proxy0的class文件
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");

        IHello iHello = (IHello) Proxy.newProxyInstance(IHello.class.getClassLoader(),
                new Class[]{IHello.class} ,
                new HWInvocationHandler(new Hello()));
        iHello.sayHello();
    }
}