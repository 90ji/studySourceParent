package com.test02;

import com.test02.callbackStrategy.TargetInterceptor;
import com.test02.callbackStrategy.TargetResultFixed;
import com.test02.generatorStrategy.ClassLoaderAwareGeneratorStrategy;
import com.test02.namingPolicy.SpringNamingPolicy;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.Test;

public class TestCglib {

    /**
     * MethodInterceptor:在调用目标方法时，CGLib会回调MethodInterceptor接口方法拦截，来实现你自己的代理逻辑，类似于JDK中的InvocationHandler接口
     * <p>
     * 在CGLib回调时可以设置对不同方法执行不同的回调逻辑，或者根本不执行回调。在JDK动态代理中并没有类似的功能，对InvocationHandler接口方法的调用对代理类内的所以方法都有效。
     *
     * @param args
     */

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(TargetObject.class);
        enhancer.setCallback(new TargetInterceptor());
        TargetObject targetObject2 = (TargetObject) enhancer.create();
        targetObject2.toString();
        targetObject2.method1("mmm1");
        targetObject2.method2(100);
        targetObject2.method3(200);
    }

    /**
     * MethodInterceptor:在调用目标方法时，CGLib会回调MethodInterceptor接口方法拦截，来实现你自己的代理逻辑，类似于JDK中的InvocationHandler接口
     */
    @Test
    public void TestMethodInterceptor() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(TargetObject.class);
        enhancer.setCallback(new TargetInterceptor());
        TargetObject targetObject2 = (TargetObject) enhancer.create();
        targetObject2.toString();
        targetObject2.method1("mmm1");
        targetObject2.method2(100);
        targetObject2.method3(200);
    }

    /**
     * CallbackFilter:在CGLib回调时可以设置对不同方法执行不同的回调逻辑，或者根本不执行回调。
     * 在JDK动态代理中并没有类似的功能，对InvocationHandler接口方法的调用对代理类内的所以方法都有效。
     */
    @Test
    public void TestCallbackFilter() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(TargetObject.class);
        CallbackFilter callbackFilter = new TargetMethodCallbackFilter();

        /**
         * (1)targetInterceptor：方法拦截器
         * (2)NoOp.INSTANCE：这个NoOp表示no operator，即什么操作也不做，代理类直接调用被代理的方法不进行拦截。
         * (3)FixedValue：表示锁定方法返回值，无论被代理类的方法返回什么值，回调方法都返回固定值。
         */
        Callback noopCb = NoOp.INSTANCE;
        Callback targetInterceptor = new TargetInterceptor();
        Callback fixedValue = new TargetResultFixed();
        Callback[] cbarray = new Callback[]{targetInterceptor, noopCb, fixedValue};
        //enhancer.setCallback(new TargetInterceptor());
        enhancer.setCallbacks(cbarray);
        enhancer.setCallbackFilter(callbackFilter);
        TargetObject targetObject2 = (TargetObject) enhancer.create();
        System.out.println(targetObject2);
        System.out.println(targetObject2.method1("mmm1"));
        System.out.println(targetObject2.method2(100));
        System.out.println(targetObject2.method3(100));
        System.out.println(targetObject2.method3(200));
    }
//    private static final Class<?>[] CALLBACK_TYPES = new Class<?>[]{NoOp.class, LookupOverrideMethodInterceptor.class, ReplaceOverrideMethodInterceptor.class};


    /**
     *
     */
    @Test
    public void TestCGLib() {
        Enhancer enhancer = new Enhancer();
        //设置需要增强的类
        enhancer.setSuperclass(TargetObject.class);
        //生成类的名字的策略
//        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
        //设置策略来使用这个生成器创建字节码。
//        enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(getClassLoader()));
//        //设置用于将生成的类的方法映射到特定回调索引的{@link CallbackFilter}。
//        enhancer.setCallbackFilter(new MethodOverrideCallbackFilter(new RootBeanDefinition()));
//        //
//        enhancer.setCallbackTypes(CALLBACK_TYPES);
        //生成代理类
        Object o = enhancer.create();
    }
    /**
     *
     */
    @Test
    public void TestCGLib01() {
        Enhancer enhancer = new Enhancer();
        //设置需要增强的类
        enhancer.setSuperclass(TargetObject.class);
        //
//        enhancer.setCallbackFilter(new MethodOverrideCallbackFilter(new RootBeanDefinition()));
        //
//        enhancer.setCallbackTypes(CALLBACK_TYPES);
        //生成类的名字的策略
        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
        //设置策略来使用这个生成器创建字节码。
        enhancer.setStrategy(new ClassLoaderAwareGeneratorStrategy(getClassLoader()));
        //生成代理类
        Object o = enhancer.create();
        //设置增强的方式
//        enhancer.setCallback();
        //
//        enhancer.setCallbacks();
        //
//        enhancer.setCallbackType();
        //设置要实现的接口
//        enhancer.setInterfaces();
        //创建一个新类而不是实例化对象
        Class clazz = enhancer.createClass();
        /**跳过*/
//        enhancer.setSerialVersionUID();
        //设置增强对象实例是否应该实现{@link Factory}接口.默认为true
        enhancer.setUseFactory(true);
        /**跳过*/
//        enhancer.generateClass();
        //设置是否拦截代理构造方法中调用的方法,默认为true
        enhancer.setInterceptDuringConstruction(true);
        //设置类加载器
        enhancer.setClassLoader(getClassLoader());
    }

    private ClassLoader getClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }



    public static String getId(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(obj));
    }
}