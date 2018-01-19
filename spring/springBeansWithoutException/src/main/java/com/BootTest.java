package com;

import beans.MyTestBean;
import beans.SpringContextUtil;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;

/**
 * Create by qxz on 2017/12/13
 * Description:
 */
public class BootTest {

    @Test
    public void test01(){
        ApplicationContext ctx=new ClassPathXmlApplicationContext("classpath:application.xml");
//        MyTestBean myTestBean =(MyTestBean) ctx.getBean("myTestBean");
//        System.out.println(JSON.toJSONString(myTestBean));
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        MyTestBean bean = SpringContextUtil.getBean(MyTestBean.class,"myTestBean");
        System.out.println(JSON.toJSONString(bean));
    }

    @Test
    public void testSimpleLoad(){
        BeanFactory bf = new XmlBeanFactory(new ClassPathResource( "application.xml"));
        MyTestBean bean=bf.getBean("myTestBean",MyTestBean.class);

        assertEquals("hello Horld!",bean.getTestStr());
        System.out.println("success!");
    }
}
