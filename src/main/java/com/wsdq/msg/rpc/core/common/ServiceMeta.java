package com.wsdq.msg.rpc.core.common;

import lombok.Data;

@Data
public class ServiceMeta {

    private String serviceName;

    private String serviceVersion;

    private String serviceAddr;

    private int servicePort;

}
