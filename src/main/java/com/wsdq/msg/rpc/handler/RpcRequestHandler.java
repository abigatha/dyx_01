package com.wsdq.msg.rpc.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wsdq.msg.domain.websocket.entity.WebSocketEntity;
import com.wsdq.msg.rpc.core.common.MyRpcRequest;
import com.wsdq.msg.rpc.core.common.MyRpcResponse;
import com.wsdq.msg.rpc.core.common.RpcServiceHelper;
import com.wsdq.msg.rpc.protocol.MyRpcProtocol;
import com.wsdq.msg.rpc.protocol.MsgHeader;
import com.wsdq.msg.rpc.protocol.MsgStatus;
import com.wsdq.msg.rpc.protocol.MsgType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<MyRpcProtocol<MyRpcRequest>> {

    private final Map<String, Object> rpcServiceMap;

    public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }

    @Override
    public final void channelRead0(ChannelHandlerContext ctx, MyRpcProtocol<MyRpcRequest> protocol) throws Exception {

        RpcRequestProcessor.submitRequest(() -> {

            // tcp 通讯协议对象  二进制传输此数据
            MyRpcProtocol<MyRpcResponse> resProtocol = new MyRpcProtocol<>();
            // 返回对象
            MyRpcResponse response = new MyRpcResponse();

            MsgHeader header = protocol.getHeader();

            header.setMsgType((byte) MsgType.RESPONSE.getType());

            try {

                Object result = handle(protocol.getBody());

                response.setData(result);

                header.setStatus((byte) MsgStatus.SUCCESS.getCode());

                resProtocol.setHeader(header);

                resProtocol.setBody(response);

            } catch (Throwable throwable) {

                header.setStatus((byte) MsgStatus.FAIL.getCode());

                response.setMessage(throwable.toString());

                log.error("process request {} error", header.getRequestId(), throwable);
            }

            ctx.writeAndFlush(resProtocol);
        });
    }

    private Object handle(MyRpcRequest request) throws Exception {

        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getServiceVersion());

        Object serviceBean = rpcServiceMap.get(serviceKey);

        if (serviceBean == null) {
            throw new RuntimeException(String.format("rpc-server invoke service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        }

        Class<?> serviceClass = serviceBean.getClass();

        String methodName = request.getMethodName();

        Class<?>[] parameterTypes = request.getParameterTypes();


        Object[] parameters = request.getParams();


        if (parameters[0] instanceof JSONObject){
            parameters[0] = JSON.toJavaObject((JSONObject) parameters[0], WebSocketEntity.class);
        }
        else if (parameters[0] instanceof WebSocketEntity) {
        } else{
            throw new RuntimeException("参数转换失败");
        }


        FastClass fastClass = FastClass.create(serviceClass);

        int methodIndex = fastClass.getIndex(methodName, parameterTypes);

        return fastClass.invoke(methodIndex, serviceBean, parameters);

    }
}
