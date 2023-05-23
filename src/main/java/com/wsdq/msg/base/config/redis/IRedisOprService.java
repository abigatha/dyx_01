package com.wsdq.msg.base.config.redis;

import java.util.concurrent.TimeUnit;

public interface IRedisOprService<T> {

    T getCacheValue(String key);


    boolean putCacheValue(String key, String keyType, T o);


    boolean updateKeyTimeout(String key, Long timeout, TimeUnit timeUnit);




}
