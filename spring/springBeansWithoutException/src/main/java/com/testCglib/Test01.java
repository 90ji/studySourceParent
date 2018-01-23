package com.testCglib;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test01 {

    public static void main(String[] args) {
        ApplicationContext bf = new ClassPathXmlApplicationContext("application-cglib.xml");
        GetBeanTest test = (GetBeanTest) bf.getBean("getBeanTest");
        test.showMe();
    }
}