package com.wsdq.msg.domain.websocket.entity;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class WebSocketEntity implements Serializable {


    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgTo() {
        return msgTo;
    }

    public void setMsgTo(String msgTo) {
        this.msgTo = msgTo;
    }

    private Long msgId;


    private String msgContent;


    private String msgTo;













}
