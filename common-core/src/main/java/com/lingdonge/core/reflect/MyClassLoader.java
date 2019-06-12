package com.lingdonge.core.reflect;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

/**
 * Classloader的加载机制是让自己的父classloader加载如果加载不到再有自己加载。如果有父classloader会一直向上询问。
 * Created by Kyle on 16/11/3.
 */
@Slf4j
public class MyClassLoader extends ClassLoader {

    /**
     *
     */
    private native void encrypt();

    public byte[] bytes;
    public String classDir;
    private String LocalName;

    private boolean Flag(FileInputStream fis, ByteArrayOutputStream bos) throws Exception {

        boolean Result = false;
        if (fis.read() == 0xCA) {
            Result = true;
        }
        return Result;

    }

    /**
     * 改变文件搜索地址，这样，在任何父classloader加载不到的情况下，才会由他加载。
     *
     * @param arg0
     * @return
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Class<?> findClass(String arg0) throws ClassNotFoundException {

        String name;
        if (LocalName != null) {
            name = LocalName;
        } else {
            name = arg0;
        }

        String ClassName = name.substring(name.lastIndexOf('.') + 1) + ".class";
        String classFileName = System.getProperty("user.dir") + "\\cn\\drawingbox\\" + ClassName;
        try {
            FileInputStream fis = new FileInputStream(classFileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            fis.close();
            //这里调用JNI函数进行解码
            //System.getProperty().setProperties(String key,String value)
            //System.setProperties(arg0);
            System.load(classDir + "\\encrypt_main.dll");
            //  System.loadLibrary("encrypt_main");
            MyClassLoader encypt_function = new MyClassLoader();
            encypt_function.classDir = classDir;
            encypt_function.bytes = bos.toByteArray();
            encypt_function.encrypt();

            ///////
            LocalName = null;
            return defineClass(encypt_function.bytes, 0, encypt_function.bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        boolean Result = false;
        String ClassName = name.substring(name.lastIndexOf('.') + 1) + ".class";
        if (ClassName.equals("Foo.class") || ClassName.equals("bar.class")) {
            String classFileName = classDir + "\\" + ClassName;
            try {
                FileInputStream fis = new FileInputStream(classFileName);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Result = Flag(fis, bos);
                fis.close();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Result == true) {
                LocalName = name;
                return super.loadClass("ThisIsJoy");
            } else {
                LocalName = null;
                return super.loadClass(name);
            }
        } else {
            return super.loadClass(name);
        }
    }
}