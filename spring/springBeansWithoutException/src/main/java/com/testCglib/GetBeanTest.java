package com.testCglib;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public abstract class GetBeanTest {
    public void showMe(){
        this.getBean().showMe();
    }
    public abstract User getBean();
}
