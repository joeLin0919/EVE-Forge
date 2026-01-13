package org.eveforge.controller;

import org.eveforge.config.BusiException;
import org.eveforge.service.IItemService;
import org.eveforge.service.ILoginService;
import org.eveforge.util.ApiResponse;
import org.eveforge.service.IMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * 测试控制器
 * 用于测试 ESI 服务功能
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private IMarketService marketService;

    @Autowired
    private IItemService itemService;

    @Autowired
    private ILoginService loginService;

    /**
     * 测试获取市场订单信息 - 按星域ID
     * 访问: http://localhost:8080/test/market-orders?regionId=10000002
     */
    @GetMapping("/market-orders")
    public ApiResponse<?> testGetMarketOrders(@RequestParam Integer regionId) {
        var orders = marketService.getMarketOrders(regionId);
        return ApiResponse.success(orders.getFirst());
    }

    /**
     * 测试获取市场订单信息 - 按星域ID和物品类型ID
     * 访问: http://localhost:8080/test/market-orders-by-type?regionId=10000002&typeId=34
     */
    @GetMapping("/market-orders-by-type")
    public ApiResponse<?> testGetMarketOrdersByType(@RequestParam Integer regionId, @RequestParam Integer typeId) {
        var orders = marketService.getMarketOrders(regionId, typeId);
        return ApiResponse.success(orders);
    }

    /**
     * 测试业务异常
     * 访问: http://localhost:8080/test/business-exception
     */
    @GetMapping("/business-exception")
    public ApiResponse<String> testBusinessException() {
        throw new BusiException(400, "这是一个业务异常示例");
    }

    /**
     * 测试获取商品价格信息
     * 访问: http://localhost:8080/test/product-price?regionId=10000002&solarSystemId=30000142&typeId=34
     */
    @GetMapping("/product-price")
    public ApiResponse<?> testGetProductPrice(@RequestParam Integer regionId, 
                                             @RequestParam Integer solarSystemId, 
                                             @RequestParam Integer typeId) {
        var productPrice = marketService.getProductPrice(regionId, solarSystemId, typeId);
        return ApiResponse.success(productPrice);
    }

    @GetMapping("/init-product-info")
    public void initProductInfo() {
        itemService.initProductInfo();
    }

    /**
     * 获取EVE SSO登录URL
     * 访问: http://localhost:8080/test/eve-login
     */
    @GetMapping("/eve-login")
    public ApiResponse<String[]> testEveLogin() {
        // 定义需要的权限范围，可以根据需要调整
        // 这里使用默认的公开信息权限
        String[] loginInfo = loginService.getLoginUrl(
            Arrays.asList("publicData"), 
            "http://localhost:8080/authority/callback"
        );
        
        System.out.println("EVE SSO Login URL: " + loginInfo[0]);
        System.out.println("State parameter: " + loginInfo[1]);
        
        return ApiResponse.success(loginInfo);
    }
}

// 单独创建一个控制器处理回调，因为回调URL不在/test路径下
@RestController
class AuthCallbackController {
    
    @Autowired
    private ILoginService loginService;
    
    /**
     * 处理EVE SSO回调
     * EVE会将用户重定向到此URL并附带授权码
     */
    @GetMapping("/authority/callback")
    public String handleAuthCallback(@RequestParam String code, @RequestParam String state) {
        try {
            System.out.println("Received authorization code: " + code);
            System.out.println("Received state: " + state);
            
            // 使用授权码获取访问令牌
            var tokenData = loginService.getToken(code);
            
            return "Authentication successful! Token information printed to console.";
        } catch (Exception e) {
            System.err.println("Error processing auth callback: " + e.getMessage());
            e.printStackTrace();
            return "Authentication failed: " + e.getMessage();
        }
    }
}
