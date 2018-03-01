package com.myTest.methodOverride;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public class ReplaceOverride extends MethodOverride {

    private final String methodReplacerBeanName;

    private List<String> typeIdentifiers = new LinkedList<String>();


    /**
     * Construct a new ReplaceOverride.
     * @param methodName the name of the method to override
     * @param methodReplacerBeanName the bean name of the MethodReplacer
     */
    public ReplaceOverride(String methodName, String methodReplacerBeanName) {
        super(methodName);
        this.methodReplacerBeanName = methodReplacerBeanName;
    }


    /**
     * Return the name of the bean implementing MethodReplacer.
     */
    public String getMethodReplacerBeanName() {
        return this.methodReplacerBeanName;
    }

    /**
     * Add a fragment of a class string, like "Exception"
     * or "java.lang.Exc", to identify a parameter type.
     * @param identifier a substring of the fully qualified class name
     */
    public void addTypeIdentifier(String identifier) {
        this.typeIdentifiers.add(identifier);
    }

    @Override
    public boolean matches(Method method) {
        if (!method.getName().equals(getMethodName())) {
            return false;
        }
        if (!isOverloaded()) {
            // Not overloaded: don't worry about arg type matching...
            return true;
        }
        // If we get here, we need to insist on precise argument matching...
        if (this.typeIdentifiers.size() != method.getParameterTypes().length) {
            return false;
        }
        for (int i = 0; i < this.typeIdentifiers.size(); i++) {
            String identifier = this.typeIdentifiers.get(i);
            if (!method.getParameterTypes()[i].getName().contains(identifier)) {
                return false;
            }
        }
        return true;
    }
}
