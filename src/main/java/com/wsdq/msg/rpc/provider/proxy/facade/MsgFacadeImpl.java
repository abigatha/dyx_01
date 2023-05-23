package com.wsdq.msg.rpc.provider.proxy.facade;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import com.wsdq.msg.base.config.redis.IRedisOprService;
import com.wsdq.msg.base.store.WebSocketRuntime;
import com.wsdq.msg.domain.websocket.entity.WebSocketEntity;
import com.wsdq.msg.rpc.provider.proxy.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.TimeUnit;

@RpcService(serviceInterface = MsgFacade.class, serviceVersion = "1.0.0")
@Slf4j
public class MsgFacadeImpl implements MsgFacade {


    @Autowired
    @Qualifier("redisOprServiceImpl")
    private IRedisOprService iRedisOprService;


    @Override
    public int send(WebSocketEntity entity) {
        String msgTo = entity.getMsgTo();

        String msgContent = entity.getMsgContent();

        log.info("MsgFacadeImpl接收到消息 : {}" , msgContent);


        if (StringUtils.isBlank(msgTo)) {
            return -1;
        }

        // 1. 在本机 直接在本机发送
        SocketIOClient client = WebSocketRuntime.clients.get(msgTo);

        if (client == null) {
            // TODO 记录到离线消息
            return 10;
        }

        client.sendEvent(msgTo, msgContent);
        // 更新缓存，在当前时间上增加3小时。 实际应该使用lua脚本, 过期时间 = 当前过期时间 + 3小时
        iRedisOprService.updateKeyTimeout(msgTo, 3L, TimeUnit.HOURS);

        return 10;
    }


}
