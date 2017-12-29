package com.studySchema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Create by qxz on 2017/12/28
 * Description:
 */
public class MyNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("user",new UserBeanDefinitionParser());
    }

}
