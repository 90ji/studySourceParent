package com.testCglibClass.cglib;

/**
 * Create by qxz on 2018/3/2
 * Description:
 */
import com.testCglibClass.jdk.Hello;

import java.io.IOException;

public class DoCGLib {
    public static void main(String[] args) throws IOException {
        CglibProxy proxy = new CglibProxy();
        //通过生成子类的方式创建代理类
        Hello proxyImp = (Hello)proxy.getProxy(Hello.class);
        proxyImp.sayHello();
//        System.in.read();
    }
}
