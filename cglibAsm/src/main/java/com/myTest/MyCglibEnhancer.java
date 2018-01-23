package com.myTest;

import com.myTest.bean.LookupBean;
import com.myTest.bean.TargetBean;
import com.myTest.generatorStrategy.ClassLoaderAwareGeneratorStrategy;
import com.myTest.methodOverride.LookupOverride;
import com.myTest.methodOverride.MethodOverride;
import com.myTest.methodOverride.MethodOverrides;
import com.myTest.methodOverride.ReplaceOverride;
import com.myTest.namingPolicy.QxzNamingPolicy;
import com.myTest.support.BaseBeanDefinition;
import net.sf.cglib.proxy.*;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public class MyCglibEnhancer {

    private static int i = 0;
    /**
     * lookup-method 是用来静态替换对应的方法
     *
     * replaced-method 是用来直接替换某方法的实现逻辑
     */
    private static final Class<?>[] CALLBACK_TYPES = new Class<?>[]{NoOp.class, LookupOverrideMethodInterceptor.class, ReplaceOverrideMethodInterceptor.class};

    private ClassLoader getClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }

    @Test
    public void mains() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        BaseBeanDefinition beanDefinition = new BaseBeanDefinition();
        beanDefinition.setBeanClass(TargetBean.class);
        MethodOverrides methodOverrides = createMethodOverrides();
        beanDefinition.setMethodOverrides(methodOverrides);
        System.out.println("========================");
        Class<?> clazz = new MyCglibEnhancer().createEnhancedSubclass(beanDefinition);
        Constructor<?> ctor = clazz.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object instance = ctor.newInstance();
        Factory factory = (Factory) instance;
        factory.setCallbacks(new Callback[]{NoOp.INSTANCE, new LookupOverrideMethodInterceptor(beanDefinition), new ReplaceOverrideMethodInterceptor(beanDefinition)});

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!method.getName().equals("doSomething")){
                continue;
            }
            Object invoke = method.invoke(instance, 1);
            System.out.println(invoke);
        }
    }
    @Test
    public void test(){
        Method[] methods = TargetBean.class.getDeclaredMethods();
        System.out.println(methods);
    }

    private static MethodOverrides createMethodOverrides() {
        MethodOverrides methodOverrides = new MethodOverrides();
        MethodOverride methodOverride = createMethodOverride();
        methodOverrides.addOverride(methodOverride);
        return methodOverrides;
    }

    private static MethodOverride createMethodOverride() {
        if (i==0){
            return new LookupOverride(getTestLookupOverrideMethod(),"com.myTest.bean.TargetBean");
        }else if (i==1){
            return new ReplaceOverride(getTestReplaceOverrideMethod(),"com.myTest.bean.TargetBean");
        }else {
            return null;
        }
    }

    private static String getTestLookupOverrideMethod() {
        Method[] methods = LookupBean.class.getDeclaredMethods();
        return methods[0].getName();
    }
//    private static Method getTestLookupOverrideMethod() {
//        Method[] methods = LookupBean.class.getDeclaredMethods();
//        return methods[0];
//    }

    private static String getTestReplaceOverrideMethod() {
        return "com.myTest.bean.ReplaceBean";
    }

    private Class<?> createEnhancedSubclass(BaseBeanDefinition beanDefinition) {
        Enhancer enhancer = new Enhancer();
        //设置父类
        enhancer.setSuperclass(beanDefinition.getBeanClass());
        //设置命名策略
        enhancer.setNamingPolicy(QxzNamingPolicy.INSTANCE);
        //设置classloader
//        enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(getClassLoader()));
        //构建过滤规则
        enhancer.setCallbackFilter(new MyMethodOverrideCallbackFilter(beanDefinition));
        //设置增强的方法
        enhancer.setCallbackTypes(CALLBACK_TYPES);
        return enhancer.createClass();
    }

    /**
     * 支持类
     */
    private static class MyCglibBeanSupport {
        private final BaseBeanDefinition baseBeanDefinition;

        public MyCglibBeanSupport(BaseBeanDefinition baseBeanDefinition) {
            this.baseBeanDefinition = baseBeanDefinition;
        }

        public BaseBeanDefinition getBaseBeanDefinition() {
            return this.baseBeanDefinition;
        }

        @Override
        public boolean equals(Object obj) {
            return (getClass() == obj.getClass() && this.baseBeanDefinition.equals(((MyCglibBeanSupport) obj).baseBeanDefinition));
        }

        @Override
        public int hashCode() {
            return this.baseBeanDefinition.hashCode();
        }
    }

    /**
     * 添加过滤规则
     */
    public class MyMethodOverrideCallbackFilter extends MyCglibBeanSupport implements CallbackFilter {

        /**
         * Index in the CGLIB callback array for passthrough behavior,
         * in which case the subclass won't override the original class.
         */
        private static final int PASSTHROUGH = 0;

        /**
         * Index in the CGLIB callback array for a method that should
         * be overridden to provide <em>method lookup</em>.
         */
        private static final int LOOKUP_OVERRIDE = 1;

        /**
         * Index in the CGLIB callback array for a method that should
         * be overridden using generic <em>method replacer</em> functionality.
         */
        private static final int METHOD_REPLACER = 2;

        public MyMethodOverrideCallbackFilter(BaseBeanDefinition baseBeanDefinition) {
            super(baseBeanDefinition);
        }

        @Override
        public int accept(Method method) {

            MethodOverride override = getBaseBeanDefinition().getMethodOverrides().getOverride(method);
            if (override == null) {
                return PASSTHROUGH;
            } else if (override instanceof LookupOverride) {
                return LOOKUP_OVERRIDE;
            } else if (override instanceof ReplaceOverride) {
                return METHOD_REPLACER;
            }
            return PASSTHROUGH;
        }
    }

    /**
     * LookupOverrideMethodInterceptor
     */
    private class LookupOverrideMethodInterceptor extends MyCglibBeanSupport implements MethodInterceptor {

        public LookupOverrideMethodInterceptor(BaseBeanDefinition baseBeanDefinition) {
            super(baseBeanDefinition);
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            LookupOverride override = (LookupOverride)getBaseBeanDefinition().getMethodOverrides().getOverride(method);
            return proxy.invoke(obj,args);
        }
    }

    /**
     * ReplaceOverrideMethodInterceptor
     */
    private class ReplaceOverrideMethodInterceptor extends MyCglibBeanSupport implements MethodInterceptor {

        public ReplaceOverrideMethodInterceptor(BaseBeanDefinition baseBeanDefinition) {
            super(baseBeanDefinition);
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return null;
        }
    }
}
