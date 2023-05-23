package com.wsdq.msg.rpc.core.common;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MyRpcRequestHolder {

    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    public static final Map<Long, MyRpcFuture<MyRpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();

    public static final Map<String, Channel> REQUEST_ADDRESS = new ConcurrentHashMap<>();
}
