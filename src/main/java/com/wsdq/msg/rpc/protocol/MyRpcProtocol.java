package com.wsdq.msg.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyRpcProtocol<T> implements Serializable {
    private MsgHeader header;
    private T body;
}
