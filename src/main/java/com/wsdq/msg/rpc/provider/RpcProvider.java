package com.wsdq.msg.rpc.provider;


import com.wsdq.msg.rpc.codec.MyRpcDecoder;
import com.wsdq.msg.rpc.codec.MyRpcEncoder;
import com.wsdq.msg.rpc.core.common.RpcServiceHelper;
import com.wsdq.msg.rpc.core.common.ServiceMeta;
import com.wsdq.msg.rpc.handler.RpcRequestHandler;
import com.wsdq.msg.rpc.provider.proxy.annotation.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private String serverAddress;

    private final int serverPort;

    private final Map<String, Object> rpcServiceMap = new HashMap<>();

    public RpcProvider(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                log.error("start rpc server error.", e);
            }
        }).start();
    }


    private void startRpcServer() throws Exception{
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup(4);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new MyRpcEncoder())
                                    .addLast(new MyRpcDecoder())
                                    //客户端心跳检测
                                    .addLast(new RpcRequestHandler(rpcServiceMap));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture channelFuture = bootstrap.bind(this.serverPort).sync();
            log.info("server started on port {}", this.serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);

        if(rpcService != null){
            // 改用 SimpleName
            String serviceName = rpcService.serviceInterface().getSimpleName();

            String serviceVersion = rpcService.serviceVersion();


            try {
                ServiceMeta serviceMeta = new ServiceMeta();
                serviceMeta.setServiceAddr(serverAddress);
                serviceMeta.setServicePort(serverPort);
                // 改用 SimpleName
                serviceMeta.setServiceName(serviceName);
                serviceMeta.setServiceVersion(serviceVersion);

                rpcServiceMap.put(RpcServiceHelper.buildServiceKey(serviceName, serviceVersion), bean);
            } catch (Exception e) {
                log.error("failed to register service {}#{}", serviceName, serviceVersion, e);
            }

        }

        return bean;
    }
}
