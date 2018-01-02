package com.studySchema;

import org.springframework.beans.factory.FactoryBean;

/**
 * Create by qxz on 2017/12/29
 * Description:
 */
public class UserFactoryBean implements FactoryBean<User>{
    private String userInfo;
    @Override
    public User getObject() throws Exception {
        User user = new User();
        user.setId("1");
        user.setEmail("aaa.@qq.com");
        user.setUserName("bbb");
        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return User.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
