package com.wsdq.msg.rpc.core.common;

public class RpcServiceHelper {
    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("#", serviceName, serviceVersion);
    }
}
