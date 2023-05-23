package com.wsdq.msg.rpc.codec;


import com.alibaba.fastjson.JSON;
import com.wsdq.msg.rpc.core.common.MyRpcRequest;
import com.wsdq.msg.rpc.core.common.MyRpcResponse;
import com.wsdq.msg.rpc.protocol.MyRpcProtocol;
import com.wsdq.msg.rpc.protocol.MsgHeader;
import com.wsdq.msg.rpc.protocol.MsgType;
import com.wsdq.msg.rpc.protocol.ProtocolConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 *
 * 解码器
 *
 * 继承 ByteToMessageDecoder
 *
 */
public class MyRpcDecoder extends ByteToMessageDecoder {


    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    |                   数据内容 （长度不定）                          |
    +---------------------------------------------------------------+
    */
    @Override
    public final void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        // 头长度判定
        if (byteBuf.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }

        // 重置read index
        byteBuf.markReaderIndex();

        //获取魔数
        short magic = byteBuf.readShort();
        if(magic != ProtocolConstants.MAGIC){
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }

        //版本号
        byte version = byteBuf.readByte();
        //序列化算法
        byte serializeType = byteBuf.readByte();
        //报文类型
        byte msgType = byteBuf.readByte();
        //状态
        byte status = byteBuf.readByte();
        //消息ID
        long requestId = byteBuf.readLong();
        //数据长度
        int dataLength = byteBuf.readInt();

        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);

        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        MsgHeader header = new MsgHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerialization(serializeType);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setMsgLen(dataLength);

        switch (msgTypeEnum){
            case REQUEST:

                MyRpcRequest request = JSON.parseObject(data, MyRpcRequest.class);

                if(request != null){

                    MyRpcProtocol<MyRpcRequest> protocol = new MyRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    list.add(protocol);

                }

                break;
            case RESPONSE:

                MyRpcResponse response =  JSON.parseObject(data, MyRpcResponse.class);

                if (response != null) {
                    MyRpcProtocol<MyRpcResponse> protocol = new MyRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    list.add(protocol);
                }

                break;
            case HEARTBEAT:
                // TODO 实现心跳
                break;
            default:
                break;

        }



    }



}
