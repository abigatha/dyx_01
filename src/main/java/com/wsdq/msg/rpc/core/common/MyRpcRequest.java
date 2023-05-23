package com.wsdq.msg.rpc.core.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyRpcRequest implements Serializable {

    private String serviceVersion;

    private String className;

    private String methodName;

    private Object[] params;

    private Class<?>[] parameterTypes;


}
