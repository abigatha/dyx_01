package com.wsdq.msg.rpc.consumer;

import com.wsdq.msg.rpc.core.common.MyRpcFuture;
import com.wsdq.msg.rpc.core.common.MyRpcRequest;
import com.wsdq.msg.rpc.core.common.MyRpcRequestHolder;
import com.wsdq.msg.rpc.core.common.MyRpcResponse;
import com.wsdq.msg.rpc.protocol.MyRpcProtocol;
import com.wsdq.msg.rpc.protocol.MsgHeader;
import com.wsdq.msg.rpc.protocol.MsgType;
import com.wsdq.msg.rpc.protocol.ProtocolConstants;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


/**
 *
 * 对象代理类  cglib
 *
 */
@Slf4j
public class RpcInvokerCglibProxy implements MethodInterceptor {

    private final String serviceVersion;

    private final long timeout;


    public RpcInvokerCglibProxy(String serviceVersion, long timeout) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
    }


    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

        try {

            MyRpcProtocol<MyRpcRequest> protocol = new MyRpcProtocol<>();

            MsgHeader header = new MsgHeader();

            //应使用分布式ID生成器生成唯一ID
            long requestId = MyRpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();

            header.setMagic(ProtocolConstants.MAGIC);

            header.setVersion(ProtocolConstants.VERSION);

            header.setRequestId(requestId);
            //序列化类型无用，当前只是用fastjson
            header.setSerialization((byte) 0x10);
            //设置tcp通讯消息类型为请求
            header.setMsgType((byte) MsgType.REQUEST.getType());

            header.setStatus((byte) 0x1);

            protocol.setHeader(header);

            MyRpcRequest request = new MyRpcRequest();

            request.setServiceVersion(this.serviceVersion);
            // 改用 SimpleName
            request.setClassName(method.getDeclaringClass().getSimpleName());
            // 方法名
            request.setMethodName(method.getName());
            // 请求参数类型
            request.setParameterTypes(new Class[]{method.getParameterTypes()[0]});
            // 请求参数
            String clientAddr = (String)args[1];

            request.setParams(new Object[]{args[0]});

            protocol.setBody(request);
            //客户端对象，用于创建和客户端的连接，每次都新创建一个。

            MyRpcFuture<MyRpcResponse> future = new MyRpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);

            MyRpcRequestHolder.REQUEST_MAP.put(requestId, future);

            Channel channel = MyRpcRequestHolder.REQUEST_ADDRESS.get(clientAddr);

            if(channel != null && channel.isActive()){

                channel.writeAndFlush(protocol);


            }else{
                RpcConsumer rpcConsumer = new RpcConsumer();

                rpcConsumer.sendRequest(protocol, clientAddr);

            }


            Object data = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();

            log.info("==================================返回值：{}, 对象名：{}", data, o.getClass().getName());

            return 0;

        } catch (Exception e) {
            log.error("proxy invoke error .....{}", e);
            throw new RuntimeException("proxy invoke error");
        }

    }
}
