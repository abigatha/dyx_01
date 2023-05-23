package com.wsdq.msg.base.config.redis;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public class RedisJsonSerializerImpl<T> implements RedisSerializer<T> {

    /**
     * 记录日志的对象
     */
    private static final Logger log = LoggerFactory.getLogger(RedisJsonSerializerImpl.class);


    /**
     * 将对象序列化成一个 byte 数组
     *
     * @param t 对象的实例
     * @return 序列化后的结果
     * @throws SerializationException 可能会抛出序列化异常
     */
    @Override
    public byte[] serialize(T t) throws SerializationException {
        // 如果对象为空，那么就返回一个空的byte数组就好了
        if (null == t) return new byte[0];

        // 获取序列化后的对象
        String resultObject = new SerializerObject(t.getClass().getName(), JSON.toJSONString(t)).toString();
        return resultObject.getBytes(StandardCharsets.UTF_8);
    }


    /**
     * 将一个已经序列化好后的对象转换为一个真实的对象，并返回它
     *
     * @param bytes 序列化后的结果
     * @return 反序列化后的结果
     * @throws SerializationException 可能会抛出序列化异常
     */
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) return null;

        // 将其反序列化成一个字符串
        SerializerObject resultObject = JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), SerializerObject.class);
        try {
            return JSON.parseObject(resultObject.jsonObject, (Type) Class.forName(resultObject.className));
        } catch (ClassNotFoundException e) {
            log.error("类未找到：" + resultObject.className, e);
            return null;
        }
    }


    /**
     * <p>
     * 序列化对象，将对象存入这个类中然后存入redis
     * </p>
     *
     * @author XiaoHH
     * @version 1.0
     * @date 2021-12-12 星期日 09:45:54
     */
    static class SerializerObject {

        /**
         * 无参构造
         */
        public SerializerObject() {
        }

        /**
         * 全参构造
         *
         * @param className  对象的类名
         * @param jsonObject 将内容对象转换为json字符串之后的结果
         */
        public SerializerObject(String className, String jsonObject) {
            this.className = className;
            this.jsonObject = jsonObject;
        }

        /**
         * 对象的类名
         */
        private String className;

        /**
         * 将内容对象转换为json字符串之后的结果
         */
        private String jsonObject;

        public String getClassName() {
            return className;
        }

        public SerializerObject setClassName(String className) {
            this.className = className;
            return this;
        }

        public String getJsonObject() {
            return jsonObject;
        }

        public SerializerObject setJsonObject(String jsonObject) {
            this.jsonObject = jsonObject;
            return this;
        }

        /**
         * 将本对象转换为json格式
         *
         * @return 转换后的json
         */
        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

}