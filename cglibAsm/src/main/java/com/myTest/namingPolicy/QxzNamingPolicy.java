package com.myTest.namingPolicy;

import net.sf.cglib.core.DefaultNamingPolicy;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public class QxzNamingPolicy extends DefaultNamingPolicy {
    public static final QxzNamingPolicy INSTANCE = new QxzNamingPolicy();
    @Override
    protected String getTag() {
        return "ByQxzCGLIB";
    }
}
