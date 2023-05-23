package com.wsdq.msg.rpc.provider;

import com.wsdq.msg.rpc.core.common.RpcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcProviderAutoConfiguration {

    @Resource
    private RpcProperties rpcProperties;


    @Bean("rpcProvider")
    public RpcProvider rpcProvider() throws Exception {
        return new RpcProvider("0.0.0.0",9091);
    }


}
