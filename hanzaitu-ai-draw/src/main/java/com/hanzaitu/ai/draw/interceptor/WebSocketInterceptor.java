package com.hanzaitu.ai.draw.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.hanzaitu.common.config.AppProperties;
import com.hanzaitu.common.core.redis.RedisUtil;
import com.hanzaitu.common.core.result.OsStatusEnum;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.core.session.UserSession;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.*;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {



    @Autowired
    private AppProperties appProperties;

    /**
     * 握手前
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        System.out.println("握手开始");
        // 获得请求参数
        String accessToken = getPathKey(request,"k");
        if (accessToken == null) {
            return false;
        }
        if (accessToken != null) {
            // 放入属性域
            // 判断缓存中是否存在user信息
            UserSession userSession =  this.getUserSession((String) accessToken);
            if (userSession == null) {
                return false;
            }
            attributes.put("token", userSession.getUserId());
            System.out.println("用户 token " + userSession.getNickName() + " 握手成功！");
            return true;
        }
        return false;
    }

    /**
     * 握手后
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        System.out.println("握手完成");
    }


    /**
     * 获取请求路径中指定的值
     * @param request
     * @param k
     * @return
     */
    private String getPathKey(ServerHttpRequest request, String k) {
        List<String> strList = Arrays.asList(request.getURI().getPath().split("/"));
        for (String vs : strList) {
            if (vs.equals(k)) {
                try {
                    if (strList.get(strList.indexOf(vs) + 1) != null) {
                        return strList.get(strList.indexOf(vs) + 1);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private UserSession getUserSession(String token) {
        String accessTokenKey = TokenUtil.getAccessTokenKey((String) token,
                appProperties.getAuth().getAccessTokenKeyPrefix());
        Object dto = RedisUtil.get(accessTokenKey);
        if(dto instanceof String) {
            UserSession userSessionDTO = (UserSession) JSON.parseObject(dto.toString(),
                    UserSession.class);
            if (userSessionDTO == null) {
                return null;
            }
            return userSessionDTO;
        }

        return null;
    }


}