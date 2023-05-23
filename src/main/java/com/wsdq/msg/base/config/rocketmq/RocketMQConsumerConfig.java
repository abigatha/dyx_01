package com.wsdq.msg.base.config.rocketmq;

import com.corundumstudio.socketio.SocketIOClient;
import com.wsdq.msg.base.config.redis.IRedisOprService;
import com.wsdq.msg.base.store.WebSocketRuntime;
import com.wsdq.msg.domain.websocket.entity.WebSocketEntity;
import com.wsdq.msg.rpc.consumer.annotation.RpcReference;
import com.wsdq.msg.rpc.consumer.facade.MsgFacade;
import com.wsdq.msg.server.websocket.WebsocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * MQ 监听消息推送
 */
@Component
@RocketMQMessageListener(consumerGroup = "websocket-Server", topic = "websocket-t1")
@DependsOn("rpcProvider")
@Slf4j
public class RocketMQConsumerConfig implements RocketMQListener<WebSocketEntity> {


    @Autowired
    @Qualifier("redisOprServiceImpl")
    private IRedisOprService iRedisOprService;

    @RpcReference(serviceVersion = "1.0.0", timeout = 3000)
    MsgFacade msgFacade;

    @Override
    public void onMessage(WebSocketEntity entity) {


        try {

            String msgTo = entity.getMsgTo();

            String msgContent = entity.getMsgContent();

            log.info("MQ接收到消息 : {}" , msgContent);


            if (StringUtils.isBlank(msgTo)) {
                return;
            }

            // 1. 在本机 直接在本机发送
            SocketIOClient client = WebSocketRuntime.clients.get(msgTo);

            if (client != null) {

                client.sendEvent(msgTo, msgContent);
                // 更新缓存，在当前时间上增加3小时。 实际应该使用lua脚本, 过期时间 = 当前过期时间 + 3小时
                iRedisOprService.updateKeyTimeout(msgTo, 3L, TimeUnit.HOURS);
                return;

            }

            // 2. 不在本机，查询redis
            String clientInfo = (String) iRedisOprService.getCacheValue(msgTo);

            if (StringUtils.isBlank(clientInfo)) {
                // TODO 记录 到 离线消息
                return;
            }

            // 3. 如果登录本机IP  说明客户端已经下线，但是缓存还在， 清除。
            if (WebsocketServer.IP.equals(clientInfo)) {
                iRedisOprService.updateKeyTimeout(msgTo, 0L, TimeUnit.SECONDS);
                // TODO 记录 到 离线消息
                return;
            }


            // 4. 不在本机 在其他机器上，进行TCP通信，发送数据
            msgFacade.send(entity, clientInfo);


        } catch (Exception e) {
            e.printStackTrace();
            // TODO 可以设计 进行重复消费一次。
            // TODO 如果本地出错， 记录到特殊记录日志表，进行存证或人工补偿。
        }


    }
}
