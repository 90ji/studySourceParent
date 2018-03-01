package com.myTest.methodOverride;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public class LookupOverride extends MethodOverride {

    private final String beanName;

    private Method method;


    /**
     * Construct a new LookupOverride.
     * @param methodName the name of the method to override
     * @param beanName the name of the bean in the current {@code BeanFactory}
     * that the overridden method should return (may be {@code null})
     */
    public LookupOverride(String methodName, String beanName) {
        super(methodName);
        this.beanName = beanName;
    }

    /**
     * Construct a new LookupOverride.
     * @param method the method to override
     * @param beanName the name of the bean in the current {@code BeanFactory}
     * that the overridden method should return (may be {@code null})
     */
    public LookupOverride(Method method, String beanName) {
        super(method.getName());
        this.method = method;
        this.beanName = beanName;
    }


    /**
     * Return the name of the bean that should be returned by this method.
     */
    public String getBeanName() {
        return this.beanName;
    }

    /**
     * Match the specified method by {@link Method} reference or method name.
     * <p>For backwards compatibility reasons, in a scenario with overloaded
     * non-abstract methods of the given name, only the no-arg variant of a
     * method will be turned into a container-driven lookup method.
     * <p>In case of a provided {@link Method}, only straight matches will
     * be considered, usually demarcated by the {@code @Lookup} annotation.
     */
    @Override
    public boolean matches(Method method) {
        if (this.method != null) {
            return method.equals(this.method);
        }
        else {
            return (method.getName().equals(getMethodName()) && (!isOverloaded() ||
                    Modifier.isAbstract(method.getModifiers()) || method.getParameterTypes().length == 0));
        }
    }
}
