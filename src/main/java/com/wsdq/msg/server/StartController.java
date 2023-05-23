package com.wsdq.msg.server;

import com.wsdq.msg.server.websocket.WebsocketServer;
import org.springframework.context.ConfigurableApplicationContext;

public class StartController {


    private final ConfigurableApplicationContext context;

    public StartController(ConfigurableApplicationContext context) {
        this.context = context;
    }

    public void start() {

        startWebsocketServer();

        startTcpServer();

    }


    private void startWebsocketServer() {

        WebsocketServer websocketServer = (WebsocketServer) context.getBean("websocketServer");

        if (websocketServer == null) {
            System.out.println(" WebsocketServer start failed ");
            return;
        }
        websocketServer.setHostname("localhost");

        websocketServer.setPort(8092);

        websocketServer.start();

    }

    private void startTcpServer() {

        /*TcpServer tcpServer = (TcpServer)context.getBean("tcpServer");

        if(tcpServer == null){
            System.out.println(" TcpServer start failed ");
            return;
        }

        tcpServer.start();*/

    }
}
