package com.lmh.rpc.common.utils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffUtils {
    //申请一个内存空间用户缓存，避免每次序列化都要申请Buffer空间
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    //缓存Schema(Schema表示组织结构，这里表示序列化对象的结构)
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    /**
     * 获取对象的Schema
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(clazz);
            if (schema != null) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }

    /**
     * 序列化方法
     * @param object
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T object) {
        Class<T> clazz = (Class<T>) object.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(object, schema, buffer);
        }finally {
            buffer.clear();
        }
        return data;
    }

    /**
     * 反序列化，将data反序列化为指定对象
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] data, Class clazz) {
        Schema<T> schema = getSchema(clazz);
        T object = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, object, schema);
        return object;
    }
}
