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
    private static final String VERIFY_URL = "https://login.eveonline.com/oauth/verify";
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
    
    /**
     * 验证访问令牌并获取角色信息
     *
     * @param accessToken 用于验证的访问令牌
     * @return 包含角色信息的JSON节点
     */
    public JsonNode verifyToken(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("User-Agent", "EVE-Forge Application");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.exchange(
            VERIFY_URL,
            org.springframework.http.HttpMethod.GET,
            entity,
            String.class
        ).getBody();

        return objectMapper.readTree(jsonResponse);
    }
    
    /**
     * 使用刷新令牌获取新的访问令牌
     *
     * @param refreshToken 用于刷新访问令牌的刷新令牌
     * @return 包含新访问令牌的JSON节点
     */
    public JsonNode refreshToken(String refreshToken) throws Exception {
        // Encode client credentials in Base64
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedCredentials);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.postForObject(TOKEN_URL, request, String.class);
        
        if (jsonResponse == null || jsonResponse.contains("error")) {
            throw new RuntimeException("Failed to refresh token: " + jsonResponse);
        }

        return objectMapper.readTree(jsonResponse);
    }
    
    /**
     * 检查访问令牌是否即将过期
     *
     * @param expiresAt 令牌过期时间戳（秒）
     * @param threshold 提前检查的阈值（秒），默认300秒（5分钟）
     * @return 如果令牌即将过期则返回true
     */
    public boolean isTokenExpiring(long expiresAt, int threshold) {
        long currentTime = System.currentTimeMillis() / 1000; // 转换为秒
        return (expiresAt - currentTime) <= threshold;
    }
    
    /**
     * 检查访问令牌是否即将过期（使用默认阈值5分钟）
     *
     * @param expiresAt 令牌过期时间戳（秒）
     * @return 如果令牌即将过期则返回true
     */
    public boolean isTokenExpiring(long expiresAt) {
        return isTokenExpiring(expiresAt, 300); // 默认提前5分钟刷新
    }
}