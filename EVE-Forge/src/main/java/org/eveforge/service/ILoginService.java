package org.eveforge.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public interface ILoginService {
    /**
     * 获取EVE SSO登录URL
     *
     * @param scopes 权限范围列表
     * @param redirectUri 回调地址
     * @return 包含登录URL和状态参数的数组
     */
    String[] getLoginUrl(List<String> scopes, String redirectUri);

    /**
     * 处理SSO回调并获取token
     *
     * @param authorizationCode 授权码
     * @return 包含访问令牌和刷新令牌的JSON节点
     */
    JsonNode getToken(String authorizationCode) throws Exception;
}
