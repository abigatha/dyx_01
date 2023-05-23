package com.wsdq.msg.rpc.consumer.facade;

import com.wsdq.msg.domain.websocket.entity.WebSocketEntity;

public interface MsgFacade {


    int send(WebSocketEntity entity, String clientAddr);


}
