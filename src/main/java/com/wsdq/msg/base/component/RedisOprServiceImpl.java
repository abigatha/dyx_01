package com.wsdq.msg.base.component;

import com.wsdq.msg.base.config.redis.IRedisOprService;
import com.wsdq.msg.base.enums.RedisKeyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component("redisOprServiceImpl")
public class RedisOprServiceImpl<T> implements IRedisOprService<T> {

    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;


    @Override
    public T getCacheValue(final String key) {
        try {
            return (T)redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public boolean putCacheValue(final String key, final String keyType,final T o) {

        try {
            redisTemplate.opsForValue().set(key, o,
                    RedisKeyType.of(keyType).getTimeout(),RedisKeyType.of(keyType).getTimeUnit());
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @Override
    public boolean updateKeyTimeout(final String key, Long timeout, TimeUnit timeUnit) {

        try {
            return redisTemplate.expire(key, timeout, timeUnit);
        } catch (Exception e) {
            return false;
        }

    }
}
