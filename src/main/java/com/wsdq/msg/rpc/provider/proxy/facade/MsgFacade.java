package com.wsdq.msg.rpc.provider.proxy.facade;

import com.wsdq.msg.domain.websocket.entity.WebSocketEntity;

public interface MsgFacade {


    int send(WebSocketEntity entity);


}
