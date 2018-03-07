package com.qinxiaozhou.study.core;

import com.alibaba.fastjson.JSON;
import com.qinxiaozhou.study.core.beans.MyTestBean;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import static org.junit.Assert.assertEquals;

/**
 * Create by qxz on 2017/12/13
 * Description:
 */
public class BootTest {

    @Test
    public void test01() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:application.xml");
        ctx.start();
//        MyTestBean myTestBean =(MyTestBean) ctx.getBean("myTestBean");
//        System.out.println(JSON.toJSONString(myTestBean));

    }

    @Test
    public void testSimpleLoad() {
        BeanFactory bf = new XmlBeanFactory(new ClassPathResource("application.xml"));
        MyTestBean bean = (MyTestBean) bf.getBean("myTestBean");

        assertEquals("hello Horld!", bean.getTestStr());
    }

    @Test
    public void testPID() {
        Integer PID = -1;
        try {
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            String name = runtime.getName(); // format: "pid@hostname"
            PID = Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Throwable e) {
            PID = 0;
        }
        System.out.println(PID);
    }
}
