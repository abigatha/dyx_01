package com.wsdq.msg.rpc.consumer;

import com.wsdq.msg.rpc.codec.MyRpcDecoder;
import com.wsdq.msg.rpc.codec.MyRpcEncoder;
import com.wsdq.msg.rpc.core.common.MyRpcRequest;
import com.wsdq.msg.rpc.core.common.MyRpcRequestHolder;
import com.wsdq.msg.rpc.core.common.ServiceMeta;
import com.wsdq.msg.rpc.handler.RpcResponseHandler;
import com.wsdq.msg.rpc.protocol.MyRpcProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcConsumer {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;


    public RpcConsumer(){

        bootstrap = new Bootstrap();

        eventLoopGroup = new NioEventLoopGroup(4);

        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new MyRpcEncoder())
                                .addLast(new MyRpcDecoder())
                                .addLast(new RpcResponseHandler());
                    }
                });

    }


    public void sendRequest(MyRpcProtocol<MyRpcRequest> protocol, String clientAddr) throws Exception {

        //MiniRpcRequest request = protocol.getBody();

        //Object[] params = request.getParams();

        ServiceMeta serviceMetadata = new ServiceMeta();

        serviceMetadata.setServiceAddr(clientAddr.split(":")[0]);

        serviceMetadata.setServicePort(Integer.parseInt(clientAddr.split(":")[1]));

        if (serviceMetadata != null) {

            ChannelFuture future = bootstrap.connect(serviceMetadata.getServiceAddr(), serviceMetadata.getServicePort()).sync();
            future.addListener((ChannelFutureListener) arg0 -> {
                if (future.isSuccess()) {
                    log.info("connect rpc server {} on port {} success.", serviceMetadata.getServiceAddr(), serviceMetadata.getServicePort());
                } else {
                    log.error("connect rpc server {} on port {} failed.", serviceMetadata.getServiceAddr(), serviceMetadata.getServicePort());
                    future.cause().printStackTrace();
                    eventLoopGroup.shutdownGracefully();
                }
            });

            Channel channel = future.channel();

            channel.writeAndFlush(protocol);

            MyRpcRequestHolder.REQUEST_ADDRESS.put(clientAddr,channel);

        }
    }

}
