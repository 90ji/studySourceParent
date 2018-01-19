package com.test02.classVisitor;


import org.objectweb.asm.ClassVisitor;

/**
 * Create by qxz on 2018/1/19
 * Description:
 */
public class MyClassVisitor extends ClassVisitor {
    public MyClassVisitor(int api) {
        super(api);
    }

    public MyClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }
}
