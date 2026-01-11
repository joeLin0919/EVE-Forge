package org.eveforge;

import org.eveforge.util.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class GlobalExceptionHandlerTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void testGlobalExceptionHandler() {
        String baseUrl = "http://localhost:8080";

        // 测试正常业务功能
        ResponseEntity<ApiResponse> successResponse = restTemplate.getForEntity(
                baseUrl + "/test/init-product", ApiResponse.class);
        System.out.println("Success Response: " + successResponse.getBody());

        // 测试业务异常处理
        try {
            ResponseEntity<ApiResponse> businessExceptionResponse = restTemplate.getForEntity(
                    baseUrl + "/test/business-exception", ApiResponse.class);
            System.out.println("Business Exception Response: " + businessExceptionResponse.getBody());
        } catch (Exception e) {
            System.out.println("Business Exception caught: " + e.getMessage());
        }

        // 测试运行时异常处理
        try {
            ResponseEntity<ApiResponse> runtimeExceptionResponse = restTemplate.getForEntity(
                    baseUrl + "/test/runtime-exception", ApiResponse.class);
            System.out.println("Runtime Exception Response: " + runtimeExceptionResponse.getBody());
        } catch (Exception e) {
            System.out.println("Runtime Exception caught: " + e.getMessage());
        }

        // 测试空指针异常处理
        try {
            ResponseEntity<ApiResponse> npeResponse = restTemplate.getForEntity(
                    baseUrl + "/test/npe-exception", ApiResponse.class);
            System.out.println("NPE Response: " + npeResponse.getBody());
        } catch (Exception e) {
            System.out.println("NPE caught: " + e.getMessage());
        }
    }
}