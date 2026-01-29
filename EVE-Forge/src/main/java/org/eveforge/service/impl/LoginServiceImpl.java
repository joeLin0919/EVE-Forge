package org.eveforge.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.eveforge.service.ILoginService;
import org.eveforge.util.EveSsoAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private EveSsoAuthUtil eveSsoAuthUtil;

    @Override
    public String[] getLoginUrl(List<String> scopes, String redirectUri) {
        return eveSsoAuthUtil.redirectToSso(scopes, redirectUri);
    }

    @Override
    public JsonNode getToken(String authorizationCode) throws Exception {
        JsonNode tokenData = eveSsoAuthUtil.requestToken(authorizationCode);
        
        // 打印原始token信息到控制台
        System.out.println("=== EVE SSO Raw Token Data Structure ===");
        System.out.println("Raw Token Response: " + tokenData.toPrettyString());
        System.out.println("========================================");
        
        // 打印token信息到控制台用于测试
        System.out.println("=== EVE SSO Token Info ===");
        System.out.println("Access Token: " + tokenData.get("access_token"));
        System.out.println("Token Type: " + tokenData.get("token_type"));
        System.out.println("Expires In: " + tokenData.get("expires_in"));
        if (tokenData.has("refresh_token")) {
            System.out.println("Refresh Token: " + tokenData.get("refresh_token"));
        }
        System.out.println("Character ID: " + tokenData.get("character_id"));
        System.out.println("Character Name: " + tokenData.get("character_name"));
        System.out.println("Character Owner Hash: " + tokenData.get("character_owner_hash"));
        System.out.println("Scopes: " + tokenData.get("scopes"));
        System.out.println("========================");
        
        return tokenData;
    }
}