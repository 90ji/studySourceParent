package com.myTest.methodOverride;

import java.lang.reflect.Method;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public abstract class MethodOverride {
    private final String methodName;

    private boolean overloaded = false;

    protected MethodOverride(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    protected void setOverloaded(boolean overloaded) {
        this.overloaded = overloaded;
    }

    protected boolean isOverloaded() {
        return this.overloaded;
    }

    public abstract boolean matches(Method method);
}
