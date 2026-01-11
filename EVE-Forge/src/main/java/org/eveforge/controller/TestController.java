package org.eveforge.controller;

import org.eveforge.config.BusiException;
import org.eveforge.service.IItemService;
import org.eveforge.util.ApiResponse;
import org.eveforge.service.IMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
