package com.wsdq.msg.base.store;


import com.corundumstudio.socketio.SocketIOClient;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket 运行时
 */
public class WebSocketRuntime {


    //public static List<SocketIOClient> clients = new ArrayList<>();//用于保存所有客户端

    public static ConcurrentHashMap<String, SocketIOClient> clients = new ConcurrentHashMap<>();




}
