package com.wsdq.msg.rpc.codec;


import com.alibaba.fastjson.JSON;
import com.wsdq.msg.rpc.protocol.MyRpcProtocol;
import com.wsdq.msg.rpc.protocol.MsgHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * 编码器
 *
 * 继承 MessageToByteEncoder
 *
 */
public class MyRpcEncoder extends MessageToByteEncoder<MyRpcProtocol<Object>> {



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
    public final void encode(ChannelHandlerContext channelHandlerContext, MyRpcProtocol<Object> objectMiniRpcProtocol, ByteBuf byteBuf) throws Exception {

        /**
         * 解码器如何解码
         *
         * 编码器就需要如何编码
         *
         * 才能一一对应
         *
         */
        MsgHeader header = objectMiniRpcProtocol.getHeader();

        byteBuf.writeShort(header.getMagic());

        byteBuf.writeByte(header.getVersion());

        byteBuf.writeByte(header.getSerialization());

        byteBuf.writeByte(header.getMsgType());

        byteBuf.writeByte(header.getStatus());

        byteBuf.writeLong(header.getRequestId());

        byte[] data = JSON.toJSONBytes(objectMiniRpcProtocol.getBody());


        byteBuf.writeInt(data.length);


        byteBuf.writeBytes(data);


    }




}
