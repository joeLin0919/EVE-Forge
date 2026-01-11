package org.eveforge.service;

import org.eveforge.repository.dto.HistoricalOrder;
import org.eveforge.repository.dto.MarketOrder;
import org.eveforge.repository.po.HistoricalOrderObj;
import org.eveforge.repository.vo.ProductPriceVo;

import java.util.List;

public interface IMarketService {

    List<MarketOrder> getMarketOrders(Integer regionId);

    List<MarketOrder> getMarketOrders(Integer regionId, Integer typeId);

    ProductPriceVo getProductPrice(Integer regionId,Integer solarSystemId, Integer typeId);

    ProductPriceVo getPLEXPrice();

    boolean isPLEX(String itemName);
    
    /**
     * 根据物品名称查询价格信息，支持模糊匹配和数量计算
     * @param itemName 带有数量标识的物品名称，格式如 "物品名*n"
     * @return 包含价格信息的列表
     */
    List<ProductPriceVo> getProductPricesByName(String itemName);

    List<HistoricalOrderObj> getHistoricalOrders(Integer regionId, Integer typeId);


}