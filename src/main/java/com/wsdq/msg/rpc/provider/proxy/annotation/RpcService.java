package com.wsdq.msg.rpc.provider.proxy.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Rpc注解
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface RpcService {

    Class<?> serviceInterface() default Object.class;

    String serviceVersion() default "1.0";


}
