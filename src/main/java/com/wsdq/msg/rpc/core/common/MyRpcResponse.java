package com.wsdq.msg.rpc.core.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyRpcResponse implements Serializable {
    private Object data;
    private String message;
}
