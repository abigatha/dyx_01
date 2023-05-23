package com.wsdq.msg.rpc.handler;

import com.wsdq.msg.rpc.core.common.MyRpcFuture;
import com.wsdq.msg.rpc.core.common.MyRpcRequestHolder;
import com.wsdq.msg.rpc.core.common.MyRpcResponse;
import com.wsdq.msg.rpc.protocol.MyRpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcResponseHandler extends SimpleChannelInboundHandler<MyRpcProtocol<MyRpcResponse>> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MyRpcProtocol<MyRpcResponse> protocol) throws Exception {

         long requestId = protocol.getHeader().getRequestId();

         MyRpcFuture<MyRpcResponse> future = MyRpcRequestHolder.REQUEST_MAP.remove(requestId);

         future.getPromise().setSuccess(protocol.getBody());


    }



}
