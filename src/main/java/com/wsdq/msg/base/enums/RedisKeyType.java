package com.wsdq.msg.base.enums;

import java.util.concurrent.TimeUnit;

public enum RedisKeyType {


    WEBSOCKET_CLIENT_INFO("ws-cli","客户端缓存",12L, TimeUnit.HOURS);


    RedisKeyType(String keyType, String keyName, Long timeout, TimeUnit timeUnit) {
        this.keyType = keyType;
        this.keyName = keyName;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }


    public static RedisKeyType of(String keyType){

        for(RedisKeyType redisKeyType : values()){
            if(redisKeyType.keyType.equals(keyType)){
                return redisKeyType;
            }
        }

        return null;

    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    private String  keyType;

    private String  keyName;

    private Long    timeout;

    private TimeUnit timeUnit;

}
