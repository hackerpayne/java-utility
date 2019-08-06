package com.lingdonge.core.file.io;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 流处理
 */
@Slf4j
public class IOUtil extends IoUtil {

    /**
     * Object转byte[]数组
     * 一般也用作：serialize序列化时使用
     *
     * @param object
     * @return
     * @throws IOException
     */
    public static byte[] objToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    /**
     * InputStream转Obj对象
     *
     * @param inputStream
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object inputStreamToObj(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        return objectInputStream.readObject();
    }

    /**
     * Bytes数据转Obj
     * 一般也用作：deserialize反序列化时使用
     *
     * @param bytes
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object bytesToObj(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

}
