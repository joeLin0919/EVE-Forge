package org.eveforge;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eveforge.repository.dto.MarketOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class MarketOrderTest {

    @Test
    void testMarketOrderJsonProcessing() throws IOException {
        // 示例JSON数据
        String json = """
            [
              {
                "duration": 90,
                "is_buy_order": false,
                "issued": "2025-12-17T13:50:13Z",
                "location_id": 60003760,
                "min_volume": 1,
                "order_id": 7214838327,
                "price": 41.7,
                "range": "region",
                "system_id": 30000142,
                "type_id": 185,
                "volume_remain": 184760,
                "volume_total": 200000
              },
              {
                "duration": 90,
                "is_buy_order": false,
                "issued": "2025-12-17T16:29:55Z",
                "location_id": 60003760,
                "min_volume": 1,
                "order_id": 7214166388,
                "price": 41.68,
                "range": "region",
                "system_id": 30000142,
                "type_id": 185,
                "volume_remain": 185700,
                "volume_total": 200000
              },
              {
                "duration": 90,
                "is_buy_order": false,
                "issued": "2025-12-27T09:47:02Z",
                "location_id": 60003760,
                "min_volume": 1,
                "order_id": 7223086153,
                "price": 41.44,
                "range": "region",
                "system_id": 30000142,
                "type_id": 185,
                "volume_remain": 100,
                "volume_total": 100
              }
            ]
            """;

        ObjectMapper objectMapper = new ObjectMapper();
        List<MarketOrder> marketOrders = objectMapper.readValue(json, new TypeReference<List<MarketOrder>>() {});

        // 输出结果
        System.out.println("解析出 " + marketOrders.size() + " 个订单:");
        for (MarketOrder order : marketOrders) {
            System.out.println("订单ID: " + order.getOrderId() + ", 价格: " + order.getPrice() + ", 数量: " + order.getVolumeRemain());
        }
    }
}