package org.eveforge.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@org.springframework.stereotype.Component
public class EveSsoAuthUtil {

    @Value("${eve.esi.client-id}")
    private String clientId;
    @Value("${eve.esi.client-secret}")
    private String clientSecret;
    private static final String AUTH_URL = "https://login.eveonline.com/v2/oauth/authorize";
    private static final String TOKEN_URL = "https://login.eveonline.com/v2/oauth/token";
    private static final SecureRandom random = new SecureRandom();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 接收授权码并将其兑换为访问令牌和刷新令牌。
     *
     * @param authorizationCode 从SSO接收到的授权码
     * @return 包含访问令牌和刷新令牌的映射
     */
    public JsonNode requestToken(String authorizationCode) throws Exception {
        // Encode client credentials in Base64
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedCredentials);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.postForObject(TOKEN_URL, request, String.class);

        return objectMapper.readTree(jsonResponse);
    }

    /**
     * 生成一个URL以将用户重定向到SSO进行身份验证。
     *
     * @param scopes      应用程序请求访问的权限范围列表
     * @param redirectUri 授权流程完成后用户将被重定向回的URL
     * @return 包含URL和所用状态参数的字符串数组
     */
    public String[] redirectToSso(List<String> scopes, String redirectUri) {
        // Generate random state parameter
        String state = generateRandomString(16);

        StringBuilder queryString = new StringBuilder();
        queryString.append("?response_type=code");
        queryString.append("&client_id=").append(clientId);
        queryString.append("&redirect_uri=").append(encodeUrl(redirectUri));
        queryString.append("&scope=").append(String.join(" ", scopes));
        queryString.append("&state=").append(state);

        return new String[]{AUTH_URL + queryString.toString(), state};
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 要生成的随机字符串的长度
     * @return 随机字符串
     */
    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }

    /**
     * 编码URL参数
     *
     * @param url 要编码的URL
     * @return 编码后的URL
     */
    private String encodeUrl(String url) {
        return java.net.URLEncoder.encode(url, StandardCharsets.UTF_8);
    }
}