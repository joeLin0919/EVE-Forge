package org.eveforge.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * EVE Swagger Interface (ESI) API 客户端工具类
 * ESI 官方文档: https://esi.evetech.net/ui/
 */
@Component
public class EsiClient {

    private static final String ESI_BASE_URL = "https://esi.evetech.net/";
    private static final String USER_AGENT = "EVE-Forge Application";

    private final RestTemplate restTemplate;

    @Value("${eve.esi.datasource:tranquility}")
    private String datasource;

    public EsiClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 执行 GET 请求
     *
     * @param endpoint API 端点路径 (例如: "/universe/types/{type_id}/")
     * @param pathParams 路径参数
     * @param queryParams 查询参数
     * @param responseType 响应类型
     * @return 响应结果
     */
    public <T> T get(String endpoint, Map<String, Object> pathParams, 
                     Map<String, String> queryParams, Class<T> responseType) {
        String url = buildUrl(endpoint, pathParams, queryParams);
        HttpHeaders headers = createHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        ResponseEntity<T> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            entity, 
            responseType
        );
        
        return response.getBody();
    }

    /**
     * 执行 GET 请求（无路径参数）
     */
    public <T> T get(String endpoint, Map<String, String> queryParams, Class<T> responseType) {
        return get(endpoint, null, queryParams, responseType);
    }

    /**
     * 执行 GET 请求（无参数）
     */
    public <T> T get(String endpoint, Class<T> responseType) {
        return get(endpoint, null, null, responseType);
    }

    /**
     * 执行 GET 请求，支持泛型类型
     *
     * @param endpoint API 端点路径
     * @param pathParams 路径参数
     * @param queryParams 查询参数
     * @param responseType 响应类型引用
     * @return 响应结果
     */
    public <T> T get(String endpoint, Map<String, Object> pathParams,
                     Map<String, String> queryParams, ParameterizedTypeReference<T> responseType) {
        String url = buildUrl(endpoint, pathParams, queryParams);
        HttpHeaders headers = createHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        ResponseEntity<T> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            entity, 
            responseType
        );
        
        return response.getBody();
    }

    /**
     * 执行 GET 请求，支持泛型类型（无路径参数）
     */
    public <T> T getWithQueryParams(String endpoint, Map<String, String> queryParams, ParameterizedTypeReference<T> responseType) {
        return get(endpoint, null, queryParams, responseType);
    }

    /**
     * 执行 GET 请求，支持泛型类型（无参数）
     */
    public <T> T get(String endpoint, ParameterizedTypeReference<T> responseType) {
        return get(endpoint, null, null, responseType);
    }

    /**
     * 执行 GET 请求，返回 JSON 字符串
     *
     * @param endpoint API 端点路径
     * @param pathParams 路径参数
     * @param queryParams 查询参数
     * @return JSON 字符串
     */
    public String getJson(String endpoint, Map<String, Object> pathParams, Map<String, String> queryParams) {
        return get(endpoint, pathParams, queryParams, String.class);
    }

    /**
     * 执行 GET 请求，返回 JSON 字符串（无路径参数）
     */
    public String getJson(String endpoint, Map<String, String> queryParams) {
        return get(endpoint, null, queryParams, String.class);
    }

    /**
     * 执行 GET 请求，返回 JSON 字符串（无参数）
     */
    public String getJson(String endpoint) {
        return get(endpoint, null, null, String.class);
    }

    /**
     * 执行 POST 请求
     *
     * @param endpoint API 端点路径
     * @param requestBody 请求体
     * @param responseType 响应类型
     * @return 响应结果
     */
    public <T, R> R post(String endpoint, T requestBody, Class<R> responseType) {
        String url = ESI_BASE_URL + endpoint;
        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<R> response = restTemplate.postForEntity(url, entity, responseType);
        
        return response.getBody();
    }

    /**
     * 构建完整的 URL
     */
    private String buildUrl(String endpoint, Map<String, Object> pathParams, 
                           Map<String, String> queryParams) {
        String url = ESI_BASE_URL + endpoint;
        
        // 替换路径参数
        if (pathParams != null) {
            for (Map.Entry<String, Object> entry : pathParams.entrySet()) {
                url = url.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
        }
        
        // 构建查询参数
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        builder.queryParam("datasource", datasource);
        
        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        
        return builder.build().toUriString();
    }

    /**
     * 创建请求头
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept-Language", "zh");
        return headers;
    }

    /**
     * 设置数据源 (tranquility 或 singularity)
     */
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }
}