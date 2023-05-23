package com.wsdq.msg.base.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig extends CachingConfigurerSupport {

        @Autowired
        private RedisConnectionFactory redisConnectionFactory;

        @Bean("myRedisTemplate")
        public RedisTemplate<String, Object> redisTemplate(){

                RedisTemplate<String, Object> template = new RedisTemplate<>();

                JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();

                StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

                template.setKeySerializer(stringRedisSerializer);

                template.setValueSerializer(new RedisJsonSerializerImpl<>());

                template.setHashKeySerializer(stringRedisSerializer);

                template.setHashValueSerializer(new RedisJsonSerializerImpl<>());

                template.setConnectionFactory(redisConnectionFactory);

                template.afterPropertiesSet();

                return template;

        }


}



