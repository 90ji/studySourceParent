package com.studySchema;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Create by qxz on 2017/12/28
 * Description:
 */
public class UserBeanDefinitionParser extends AbstractBeanDefinitionParser {
    protected Class getBeanClass(Element element){
        return User.class;
    }
    protected void doParse(Element element, BeanDefinitionBuilder bean){
        String userName = element.getAttribute("userName");
        String email = element.getAttribute("email");
        if (StringUtils.hasText(userName)){
            bean.addPropertyValue("userName",userName);
        }
        if (StringUtils.hasText(email)){
            bean.addPropertyValue("email",email);
        }
    }
    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        return null;
    }
}
