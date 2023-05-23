package com.wsdq.msg.server.websocket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.wsdq.msg.base.config.redis.IRedisOprService;
import com.wsdq.msg.base.enums.RedisKeyType;
import com.wsdq.msg.base.store.WebSocketRuntime;
import com.wsdq.msg.base.utils.ParamsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component("websocketServer")
public class WebsocketServer {

    public static final String splitCode = "1#1#1#";

    public static final String topic = "websocket-t1";

    public static final String key = "token";

    public static final String IP = "127.0.0.0:9091";

    @Autowired
    @Qualifier("redisOprServiceImpl")
    private IRedisOprService iRedisOprService;


    private String hostname;

    private int port;


    public void start(){

        Configuration config = new Configuration();
        config.setHostname(hostname);
        config.setPort(port);

        final SocketIOServer server = new SocketIOServer(config);
        //添加创建连接的监听器
        server.addConnectListener(client -> {
            putClientInfo(client);
        });
        //添加断开连接的监听器
        server.addDisconnectListener(client -> {
            removeClientInfo(client);
        });


        //启动服务
        server.start();

    }

    public String getHostname() {

        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public void putClientInfo(SocketIOClient client){

        String name = ParamsUtil.getParamBySocketIOClient(client,key);

        WebSocketRuntime.clients.put(name,client);

        // 12 小时过期  redis key：用户ID value: 当前所在连接的IP和端口 端口默认为本机 netty server 端口
        iRedisOprService.putCacheValue(name,
                RedisKeyType.WEBSOCKET_CLIENT_INFO.getKeyType(),IP);

    }

    public void removeClientInfo(SocketIOClient client){

        String name = ParamsUtil.getParamBySocketIOClient(client,key);

        WebSocketRuntime.clients.remove(name);

        iRedisOprService.updateKeyTimeout(name,0L, TimeUnit.SECONDS);

    }

}
