package com.test02.generatorStrategy;

import net.sf.cglib.core.ClassGenerator;
import net.sf.cglib.core.DefaultGeneratorStrategy;

/**
 * Create by qxz on 2018/1/19
 * Description:
 */
public class ClassLoaderAwareGeneratorStrategy extends DefaultGeneratorStrategy {

    private final ClassLoader classLoader;

    public ClassLoaderAwareGeneratorStrategy(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public byte[] generate(ClassGenerator cg) throws Exception {
        if (this.classLoader == null) {
            return super.generate(cg);
        }

        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader;
        try {
            threadContextClassLoader = currentThread.getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
            return super.generate(cg);
        }

        boolean overrideClassLoader = !this.classLoader.equals(threadContextClassLoader);
        if (overrideClassLoader) {
            currentThread.setContextClassLoader(this.classLoader);
        }
        try {
            return super.generate(cg);
        } finally {
            if (overrideClassLoader) {
                // Reset original thread context ClassLoader.
                currentThread.setContextClassLoader(threadContextClassLoader);
            }
        }
    }
}