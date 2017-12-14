package com.test01;

import com.alibaba.fastjson.JSON;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.InterfaceMaker;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;
import org.objectweb.asm.Type;

public class TestCglib {


    @Test
    public void test01() {
        Type type = Type.getType(MethodInterceptor.class);
        System.out.println(JSON.toJSONString(type));
    }
    @Test
    public void test03() {
        boolean STRESS_HASH_CODE = Boolean.getBoolean("net.sf.cglib.test.stressHashCodes");
        System.out.println(STRESS_HASH_CODE);
    }
    @Test
    public void test02() {
        Enhancer.EnhancerKey KEY_FACTORY = (Enhancer.EnhancerKey) KeyFactory.create(Enhancer.EnhancerKey.class, KeyFactory.HASH_ASM_TYPE, null);
        System.out.println();
    }

    /**
     * 测试普通增强
     * <p>
     * <p>
     * superclass = superclass;
     * classOnly = false;
     * argumentTypes = null;
     * callbacks = callbacks
     * callbackTypes = Type.getType(MethodInterceptor.class);
     * validateCallbackTypes = true;
     * filter = new CallbackFilter() { public int accept(Method method) { return 0; } };
     */
    @Test
    public void testEnhancer() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(TargetObject.class);
        enhancer.setCallback(new TargetInterceptor());
        TargetObject targetObject2 = (TargetObject) enhancer.create();

        System.out.println(targetObject2 + "\n");
        targetObject2.method1("mmm1");
        targetObject2.method2(100);
        targetObject2.method3(200);
    }

    /**
     * 测试过滤器增强
     */
    @Test
    public void testFilter() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(TargetObject.class);
        CallbackFilter callbackFilter = new TargetMethodCallbackFilter();

        /**
         *
         (1)callback1：方法拦截器
         (2)NoOp.INSTANCE：这个NoOp表示no operator，即什么操作也不做，代理类直接调用被代理的方法不进行拦截。
         (3)FixedValue：表示锁定方法返回值，无论被代理类的方法返回什么值，回调方法都返回固定值。
         */
        Callback callback1 = new TargetInterceptor();
        Callback noopCb = NoOp.INSTANCE;
        Callback fixedValue = new TargetResultFixed();
        Callback[] cbarray = new Callback[]{callback1, noopCb, fixedValue};
        //enhancer.setCallback(new TargetInterceptor());
        enhancer.setCallbacks(cbarray);
        enhancer.setCallbackFilter(callbackFilter);
        TargetObject targetObject2 = (TargetObject) enhancer.create();

        System.out.println("***********************************");
        System.out.println(targetObject2);
        targetObject2.method1("mmm1");
        targetObject2.method2(100);
        targetObject2.method3(100);
        targetObject2.method3(200);
    }

    /**
     * 测试懒加载增强
     */
    @Test
    public void testLazy() {
        LazyBean bean = new LazyBean("aaa", 12);
        System.out.println(bean);
        System.out.println("=============");
        System.out.println(bean);
        System.out.println("=============");
        System.out.println(bean);
        System.out.println("=============");
        System.out.println(bean.getPropertyBean().getKey());
        System.out.println("=============");
        System.out.println(bean.getPropertyBean().getValue());
    }

    /**
     * 测试接口生成器增强
     */
    @Test
    public void testAll() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        InterfaceMaker interfaceMaker = new InterfaceMaker();
        //抽取某个类的方法生成接口方法
        interfaceMaker.add(TargetObject.class);
        Class<?> targetInterface = interfaceMaker.create();
        for (Method method : targetInterface.getMethods()) {
            System.out.println(method.getName());
        }
        //接口代理并设置代理接口方法拦截
        Object object = Enhancer.create(Object.class, new Class[]{targetInterface}, new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args,
                                    MethodProxy methodProxy) throws Throwable {
                if (method.getName().equals("method1")) {
                    System.out.println("filter method1 ");
                    return "mmmmmmmmm";
                }
                if (method.getName().equals("method2")) {
                    System.out.println("filter method2 ");
                    return 1111111;
                }
                if (method.getName().equals("method3")) {
                    System.out.println("filter method3 ");
                    return 3333;
                }
                return "default";
            }
        });
        Method targetMethod1 = object.getClass().getMethod("method3", new Class[]{int.class});
        int i = (int) targetMethod1.invoke(object, new Object[]{33});
        Method targetMethod = object.getClass().getMethod("method1", new Class[]{String.class});
        System.out.println(targetMethod.invoke(object, new Object[]{"sdfs"}));
    }

}