package com.myTest.support;

import com.myTest.methodOverride.MethodOverrides;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public class BaseBeanDefinition {
    private MethodOverrides methodOverrides;
    private Class beanClass;

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public MethodOverrides getMethodOverrides() {
        return methodOverrides;
    }

    public void setMethodOverrides(MethodOverrides methodOverrides) {
        this.methodOverrides = methodOverrides;
    }

    public Class getBeanClass() {
        return beanClass;
    }
}
