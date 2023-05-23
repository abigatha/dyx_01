package com.wsdq.msg.rpc.core.common;

import io.netty.util.concurrent.Promise;
import lombok.Data;

@Data
public class MyRpcFuture<T> {

    private Promise<T> promise;

    private long timeout;


    public MyRpcFuture(Promise<T> promise, long timeout) {

        this.promise = promise;

        this.timeout = timeout;

    }

}
