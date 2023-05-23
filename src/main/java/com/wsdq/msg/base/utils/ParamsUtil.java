package com.wsdq.msg.base.utils;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ParamsUtil {






    public static String getParamBySocketIOClient(SocketIOClient client, String key) {
        Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
        List<String> userIdList = params.get(key);
        if (!CollectionUtils.isEmpty(userIdList)) {
            return userIdList.get(0);
        }
        return null;
    }

}
