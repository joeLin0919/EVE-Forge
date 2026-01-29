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
@RequestMapping("/api/test")
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
        // 添加所有您指定的ESI权限范围
        String[] loginInfo = loginService.getLoginUrl(
            Arrays.asList(
                "esi-calendar.respond_calendar_events.v1",
                "esi-calendar.read_calendar_events.v1",
                "esi-location.read_location.v1",
                "esi-location.read_ship_type.v1",
                "esi-mail.organize_mail.v1",
                "esi-mail.read_mail.v1",
                "esi-mail.send_mail.v1",
                "esi-skills.read_skills.v1",
                "esi-skills.read_skillqueue.v1",
                "esi-wallet.read_character_wallet.v1",
                "esi-wallet.read_corporation_wallet.v1",
                "esi-search.search_structures.v1",
                "esi-clones.read_clones.v1",
                "esi-characters.read_contacts.v1",
                "esi-universe.read_structures.v1",
                "esi-killmails.read_killmails.v1",
                "esi-corporations.read_corporation_membership.v1",
                "esi-assets.read_assets.v1",
                "esi-planets.manage_planets.v1",
                "esi-fleets.read_fleet.v1",
                "esi-fleets.write_fleet.v1",
                "esi-ui.open_window.v1",
                "esi-ui.write_waypoint.v1",
                "esi-characters.write_contacts.v1",
                "esi-fittings.read_fittings.v1",
                "esi-fittings.write_fittings.v1",
                "esi-markets.structure_markets.v1",
                "esi-corporations.read_structures.v1",
                "esi-characters.read_loyalty.v1",
                "esi-characters.read_chat_channels.v1",
                "esi-characters.read_medals.v1",
                "esi-characters.read_standings.v1",
                "esi-characters.read_agents_research.v1",
                "esi-industry.read_character_jobs.v1",
                "esi-markets.read_character_orders.v1",
                "esi-characters.read_blueprints.v1",
                "esi-characters.read_corporation_roles.v1",
                "esi-location.read_online.v1",
                "esi-contracts.read_character_contracts.v1",
                "esi-clones.read_implants.v1",
                "esi-characters.read_fatigue.v1",
                "esi-killmails.read_corporation_killmails.v1",
                "esi-corporations.track_members.v1",
                "esi-wallet.read_corporation_wallets.v1",
                "esi-characters.read_notifications.v1",
                "esi-corporations.read_divisions.v1",
                "esi-corporations.read_contacts.v1",
                "esi-assets.read_corporation_assets.v1",
                "esi-corporations.read_titles.v1",
                "esi-corporations.read_blueprints.v1",
                "esi-contracts.read_corporation_contracts.v1",
                "esi-corporations.read_standings.v1",
                "esi-corporations.read_starbases.v1",
                "esi-industry.read_corporation_jobs.v1",
                "esi-markets.read_corporation_orders.v1",
                "esi-corporations.read_container_logs.v1",
                "esi-industry.read_character_mining.v1",
                "esi-industry.read_corporation_mining.v1",
                "esi-planets.read_customs_offices.v1",
                "esi-corporations.read_facilities.v1",
                "esi-corporations.read_medals.v1",
                "esi-characters.read_titles.v1",
                "esi-alliances.read_contacts.v1",
                "esi-characters.read_fw_stats.v1",
                "esi-corporations.read_fw_stats.v1",
                "esi-corporations.read_projects.v1",
                "esi-corporations.read_freelance_jobs.v1",
                "esi-characters.read_freelance_jobs.v1"
            ), 
            "http://eveforge.top/api/authority/callback"
        );

        return ApiResponse.success(loginInfo);
    }
}

// 单独创建一个控制器处理回调，因为回调URL不在/test路径下
@RestController
class AuthCallbackController {
    
    @Autowired
    private ILoginService loginService;
    
    @Autowired
    private org.eveforge.util.EsiClient esiClient;
    
    @Autowired
    private org.eveforge.util.EveSsoAuthUtil eveSsoAuthUtil;
    
    /**
     * 处理EVE SSO回调
     * EVE会将用户重定向到此URL并附带授权码
     */
    @GetMapping("/api/authority/callback")
    public String handleAuthCallback(@RequestParam String code, @RequestParam String state) {
        try {
            System.out.println("Received authorization code: " + code);
            System.out.println("Received state: " + state);
            
            // 使用授权码获取访问令牌
            var tokenData = loginService.getToken(code);
            
            // 从token响应中提取access token
            String accessToken = tokenData.get("access_token").asText();
            String refreshToken = tokenData.get("refresh_token").asText();
            
            // 使用access token验证并获取角色信息
            com.fasterxml.jackson.databind.JsonNode charInfo = eveSsoAuthUtil.verifyToken(accessToken);
            
            System.out.println("\n\n\n\n\n");
            System.out.println("=== Character Verification Info ===");
            System.out.println("Character Info: " + charInfo.toPrettyString());
            System.out.println("Access Token: " + accessToken);
            System.out.println("Refresh Token: " + refreshToken);
            System.out.println("=====================================");
            
            return "Authentication successful! Token information printed to console.";
        } catch (Exception e) {
            System.err.println("Error processing auth callback: " + e.getMessage());
            e.printStackTrace();
            return "Authentication failed: " + e.getMessage();
        }
    }
}
