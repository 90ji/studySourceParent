package com.myTest;

import com.myTest.bean.MethodReplacer;
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
    /**
     * 0--使用lookup-method
     * 1--使用replace-method
     */
    private static int mode = 1;
    /**
     * lookup-method 是用来静态替换对应的方法
     * <p>
     * replaced-method 是用来直接替换某方法的实现逻辑
     */
    private static final Class<?>[] CALLBACK_TYPES = new Class<?>[]{NoOp.class, LookupOverrideMethodInterceptor.class, ReplaceOverrideMethodInterceptor.class};

    private ClassLoader getClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }

    @Test
    public void mains() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        /**
         * 0--使用lookup-method
         * 1--使用replace-method
         * 其他--不改变原有逻辑
         */
        mode = 0;
        Method doSomethingMethod = TargetBean.class.getMethod("doSomething", Integer.class);

        //装配一个简单的BaseBeanDefinition
        BaseBeanDefinition beanDefinition = getBaseBeanDefinition(doSomethingMethod);

        System.out.println("============= start create ============");
        //开始创建新的对象
        Object instance = createNewObject(beanDefinition);
        System.out.println("============= end create ===========");

        //使用新对象调用原有方法
        Object invoke2 = doSomethingMethod.invoke(instance, 1);
        System.out.println("结果:" + invoke2);
    }

    private Object createNewObject(BaseBeanDefinition beanDefinition) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> clazz = new MyCglibEnhancer().createEnhancedSubclass(beanDefinition);
        Constructor<?> ctor = clazz.getDeclaredConstructor();
        ctor.setAccessible(true);
        Object instance = ctor.newInstance();
        Factory factory = (Factory) instance;
        factory.setCallbacks(new Callback[]{NoOp.INSTANCE, new LookupOverrideMethodInterceptor(beanDefinition), new ReplaceOverrideMethodInterceptor(beanDefinition)});
        return instance;
    }

    private BaseBeanDefinition getBaseBeanDefinition(Method doSomethingMethod) {
        BaseBeanDefinition beanDefinition = new BaseBeanDefinition();
        beanDefinition.setBeanClass(TargetBean.class);
        MethodOverrides methodOverrides = createMethodOverrides(doSomethingMethod);
        beanDefinition.setMethodOverrides(methodOverrides);
        return beanDefinition;
    }

    @Test
    public void test() {
        Method[] methods = TargetBean.class.getDeclaredMethods();
        System.out.println(methods);
    }

    private static MethodOverrides createMethodOverrides(Method method) {
        MethodOverrides methodOverrides = new MethodOverrides();
        MethodOverride methodOverride = createMethodOverride(method);
        methodOverrides.addOverride(methodOverride);
        return methodOverrides;
    }

    private static MethodOverride createMethodOverride(Method method) {
        if (mode == 0) {
            return new LookupOverride(method.getName(), "com.myTest.bean.LookupBean");
        } else if (mode == 1) {
            return new ReplaceOverride(method.getName(), "com.myTest.bean.ReplaceBean");
        } else {
            return null;
        }
    }

    private Class<?> createEnhancedSubclass(BaseBeanDefinition beanDefinition) {
        Enhancer enhancer = new Enhancer();
        //设置父类
        enhancer.setSuperclass(beanDefinition.getBeanClass());
        //设置命名策略
        enhancer.setNamingPolicy(QxzNamingPolicy.INSTANCE);
        //设置classloader
        enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(getClassLoader()));
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
            LookupOverride override = (LookupOverride) getBaseBeanDefinition().getMethodOverrides().getOverride(method);
            Class<?> clazz = Class.forName(override.getBeanName());
            Method method1 = clazz.getMethod(method.getName(), method.getParameterTypes());
            return method1.invoke(clazz.newInstance(), args);
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
            ReplaceOverride ro = (ReplaceOverride) getBaseBeanDefinition().getMethodOverrides().getOverride(method);
            MethodReplacer o = (MethodReplacer) Class.forName(ro.getMethodReplacerBeanName()).newInstance();
            return o.reimplement(obj, method, args);
        }
    }
}
