package com.wsdq.msg;


import com.wsdq.msg.server.StartController;
import com.wsdq.msg.server.websocket.WebsocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
public class WebSocketApplication {


    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(WebSocketApplication.class, args);

        StartController startController = new StartController(context);

        startController.start();

    }
}
