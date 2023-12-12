package com.hanzaitu.common.utils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
public class TokenUtil {


    private TokenUtil() {}

    public static String getAccessToken(HttpServletRequest request, String accessTokenName) {
        String accessToken = request.getHeader(accessTokenName);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = CookieUtil.get(request, accessTokenName);
        }
        return accessToken;
    }

    public static String createAccessToken(Object id) {
        String uuid = UUID.randomUUID().toString();
        return MD5Utils.encodeStr(uuid + "|" + id);
    }

    public static String getAccessTokenKey(String accessToken, String accessTokenKey) {
        return new StringBuilder(accessTokenKey).append(":").append(accessToken).toString();
    }
}
