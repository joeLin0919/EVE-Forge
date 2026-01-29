package org.eveforge.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eveforge.config.BusiException;
import org.eveforge.mapper.HistoricalOrderMapper;
import org.eveforge.repository.dto.HistoricalOrder;
import org.eveforge.repository.dto.MarketOrder;
import org.eveforge.repository.enums.MarketLocationEnum;
import org.eveforge.repository.po.HistoricalOrderObj;
import org.eveforge.repository.vo.ProductPriceVo;
import org.eveforge.service.IItemNgramMatchingService;
import org.eveforge.service.IItemService;
import org.eveforge.service.IMarketService;
import org.eveforge.service.ISystemService;
import org.eveforge.util.ChartUtil;
import org.eveforge.util.CommandParserUtil;
import org.eveforge.util.EsiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;

import org.eveforge.util.TextMatchingUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class MarketServiceImpl implements IMarketService {

    private final EsiClient esiClient;

    @Autowired
    @Lazy
    private ApplicationContext applicationContext;

    @Autowired
    private HistoricalOrderMapper historicalOrderMapper;

    @Autowired
    public MarketServiceImpl(EsiClient esiClient) {
        this.esiClient = esiClient;
    }

    @Autowired
    private ISystemService systemService;

    @Autowired
    private IItemService itemService;
    
    @Autowired
    private IItemNgramMatchingService itemNgramMatchingService;


    @Override
    public List<MarketOrder> getMarketOrders(Integer regionId) {
        log.info("正在从 {} 星域接收订单信息", regionId);
        String endPoint = "markets/{region}/orders";
        Map<String, Object> pathParams = Map.of("region", regionId.toString());
        List<MarketOrder> marketOrders = esiClient.get(endPoint, pathParams, null, new ParameterizedTypeReference<List<MarketOrder>>(){});
        log.info("接收到 {} 个订单信息", marketOrders.size());
        return marketOrders;
    }

    @Override
    public List<MarketOrder> getMarketOrders(Integer regionId, Integer typeId) {
        log.info("正在从 region_id:{} 星域接收 type_id:{} 订单信息", regionId, typeId);
        String endPoint = "markets/{region}/orders";
        Map<String, Object> pathParams = Map.of("region", regionId.toString());
        Map<String, String> queryParams = Map.of("type_id", typeId.toString());
        List<MarketOrder> marketOrders = esiClient.get(endPoint, pathParams, queryParams, new ParameterizedTypeReference<List<MarketOrder>>(){});
        log.info("接收到 {} 个订单信息", marketOrders.size());
        return marketOrders;
    }

    @Override
    @Cacheable(value = "productPrice", key = "#regionId + '_' + #solarSystemId + '_' + #typeId")
    public ProductPriceVo getProductPrice(Integer regionId, Integer solarSystemId, Integer typeId) {
        List<MarketOrder> marketOrders = getMarketOrders(regionId, typeId).stream()
                .filter(marketOrder -> marketOrder.getSystemId().equals(solarSystemId))
                .toList();
        ProductPriceVo productPriceVo = new ProductPriceVo();
        productPriceVo.setName(itemService.getItemNameById(typeId));
        productPriceVo.setBuyPrice(getHighestBuyPrice(marketOrders));
        productPriceVo.setSellPrice(getLowestSellPrice(marketOrders));
        Double middlePrice = (productPriceVo.getBuyPrice() + productPriceVo.getSellPrice())/2;
        productPriceVo.setMiddlePrice(middlePrice);
        
        // 如果买入价和卖出价都为0，则返回null
        if (productPriceVo.getBuyPrice() == 0.0 && productPriceVo.getSellPrice() == 0.0) {
            return null;
        }
        return productPriceVo;
    }

    @Override
    public ProductPriceVo getPLEXPrice() {
        List<MarketOrder> marketOrders = getMarketOrders(MarketLocationEnum.PLEX.getRegionId(), 44992); // PLEX的type_id是44992
        ProductPriceVo productPriceVo = new ProductPriceVo();
        productPriceVo.setName("PLEX");
        productPriceVo.setBuyPrice(getHighestBuyPrice(marketOrders));
        productPriceVo.setSellPrice(getLowestSellPrice(marketOrders));
        productPriceVo.setMiddlePrice((productPriceVo.getBuyPrice() + productPriceVo.getSellPrice())/2);
        return productPriceVo;
    }

    private Double getHighestBuyPrice(List<MarketOrder> marketOrders) {
        return marketOrders.stream()
                .filter(MarketOrder::getIsBuyOrder)
                .mapToDouble(MarketOrder::getPrice)
                .max()
                .orElse(0);
    }

    private Double getLowestSellPrice(List<MarketOrder> marketOrders) {
        return marketOrders.stream()
                .filter(marketOrder -> !marketOrder.getIsBuyOrder())
                .mapToDouble(MarketOrder::getPrice)
                .min()
                .orElse(0);
    }
    
    @Override
    public List<ProductPriceVo> getProductPricesByName(String itemName) {
        // 解析物品名称和数量
        String[] parsedResult = CommandParserUtil.parseItemNameAndQuantity(itemName);
        String actualItemName = parsedResult[0];
        int quantity = Integer.parseInt(parsedResult[1]);
        
        // 创建DecimalFormat用于价格格式化
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        
        // 检查是否是PLEX
        if (isPLEX(actualItemName)) {
            ProductPriceVo plexPrice = getPLEXPrice();
            // 计算总价
            double totalBuyPrice = CommandParserUtil.calculateTotalPrice(plexPrice.getBuyPrice(), quantity);
            double totalSellPrice = CommandParserUtil.calculateTotalPrice(plexPrice.getSellPrice(), quantity);
            double totalMiddlePrice = CommandParserUtil.calculateTotalPrice(plexPrice.getMiddlePrice(), quantity);
            // 设置总价
            plexPrice.setBuyPrice(totalBuyPrice);
            plexPrice.setSellPrice(totalSellPrice);
            plexPrice.setMiddlePrice(totalMiddlePrice);
            
            // 设置显示名称
            String itemDisplay = quantity > 1 ? 
                String.format("%s*%d", plexPrice.getName(), quantity) : 
                plexPrice.getName();
            plexPrice.setName(itemDisplay);
            return List.of(plexPrice);
        } else {
            // 使用 findAllMatches 方法获取所有匹配项
            List<TextMatchingUtil.MatchingResult> allMatches = itemNgramMatchingService.findAllMatches(actualItemName);
            
            if (allMatches.isEmpty()) {
                return List.of(); // 返回空列表表示未找到匹配项
            }
            List<ProductPriceVo> results = new java.util.ArrayList<>(allMatches.stream()
                    .limit(6) // 最多返回6个结果
                    .map(matchResult -> {
                        String matchedItemName = matchResult.getMatchedText();
                        List<Integer> itemIdList = itemService.getItemIdByName(matchedItemName);
                        Integer itemId = itemIdList.isEmpty() ? null : itemIdList.getFirst();

                        if (itemId != null) {
                            ProductPriceVo productPriceVo = applicationContext.getBean(MarketServiceImpl.class).getProductPrice(
                                    MarketLocationEnum.JITA.getRegionId(),
                                    MarketLocationEnum.JITA.getSystemId(),
                                    itemId);

                            if (productPriceVo != null && productPriceVo.getName() != null) {
                                // 计算总价
                                double totalBuyPrice = CommandParserUtil.calculateTotalPrice(productPriceVo.getBuyPrice(), quantity);
                                double totalSellPrice = CommandParserUtil.calculateTotalPrice(productPriceVo.getSellPrice(), quantity);
                                double totalMiddlePrice = CommandParserUtil.calculateTotalPrice(productPriceVo.getMiddlePrice(), quantity);

                                // 设置总价
                                productPriceVo.setBuyPrice(totalBuyPrice);
                                productPriceVo.setSellPrice(totalSellPrice);
                                productPriceVo.setMiddlePrice(totalMiddlePrice);
                                productPriceVo.setItemId(itemId);

                                // 设置显示名称
                                String itemDisplay = quantity > 1 ?
                                        String.format("%s*%d", productPriceVo.getName(), quantity) :
                                        productPriceVo.getName();
                                productPriceVo.setName(itemDisplay);

                                return productPriceVo;
                            }
                        }
                        return null;
                    })
                    .filter(result -> result != null)
                    .toList());
            boolean isImplant = true;
            Double totalsellPrice = 0.0;
            Double totalBuyPrice = 0.0;
            for(int i=0;i<results.size();i++){
                totalsellPrice += results.get(i).getSellPrice();
                totalBuyPrice += results.get(i).getBuyPrice();
                Integer groupId = itemService.getGroupIdById(results.get(i).getItemId());
                if(groupId == null || !groupId.equals(300)){
                    isImplant = false;
                    break;
                }
            }
            if(isImplant){
                ProductPriceVo totalPrice = new ProductPriceVo();
                totalPrice.setName("总价");
                totalPrice.setSellPrice(totalsellPrice);
                totalPrice.setBuyPrice(totalBuyPrice);
                totalPrice.setMiddlePrice((totalPrice.getSellPrice() + totalPrice.getBuyPrice())/2);
                results.add(totalPrice);
            }
            return results;
        }
    }

    @Override
    public List<HistoricalOrderObj> getHistoricalOrders(Integer regionId, Integer typeId) {
        // 从数据库中获取历史订单
        List<HistoricalOrderObj> historicalOrdersObj;
        historicalOrdersObj = historicalOrderMapper.getHistoricalOrderByTypeIdAndRegionId(typeId, regionId);
        if(!CollectionUtils.isEmpty(historicalOrdersObj)){
            return historicalOrdersObj;
        }
        //数据库中没有记录，则从esi中获取
        List<HistoricalOrderObj> historicalOrderObjs = getHistoricalOrdersFromEsi(regionId, typeId);
        historicalOrderMapper.insertHistoricalOrders(historicalOrderObjs);
        return historicalOrderObjs;
    }

    @Async
    @Scheduled(cron = "0 10 20 * * ?")
    public void updateHistoricalOrders() {
        log.info("开始更新已存在的历史订单");
        List<Integer> typeIdList = historicalOrderMapper.getAllTypeIds();
        log.info("已存在历史订单的typeId有{}个", typeIdList.size());
        for (Integer typeId : typeIdList) {
            Integer regionId;
            List<HistoricalOrderObj> historicalOrders;
            if(typeId==44992){
                regionId=MarketLocationEnum.PLEX.getRegionId();
                historicalOrders = getHistoricalOrdersFromEsi(regionId, typeId);
            }else{
                regionId=MarketLocationEnum.JITA.getRegionId();
                historicalOrders = getHistoricalOrdersFromEsi(regionId, typeId);
            }
            Timestamp earliestDate = historicalOrders.stream()
                    .map(HistoricalOrderObj::getDate)
                    .min(Timestamp::compareTo)
                    .orElse(null);
            Timestamp latestDate = historicalOrderMapper.getLatestDate(typeId, regionId);
            if(!ObjectUtils.isEmpty(earliestDate)){
                historicalOrderMapper.deleteHistoricalOrdersByDate(typeId, regionId, earliestDate);
            }
            if(!ObjectUtils.isEmpty(latestDate)){
                List<HistoricalOrderObj> insertOrders=historicalOrders.stream()
                        .filter(historicalOrder -> historicalOrder.getDate().after(latestDate))
                        .toList();
                if(!CollectionUtils.isEmpty(insertOrders)){
                    historicalOrderMapper.insertHistoricalOrders(insertOrders);
                }

            }
        }
        log.info("更新已存在历史订单完成");
    }

    /**
     * 检查物品名称是否为PLEX
     * @param itemName 物品名称
     * @return 是否为PLEX
     */
    public boolean isPLEX(String itemName) {
        return itemName.equals("PLEX") || itemName.equals("plex") || itemName.equals("伊甸币");
    }

    private List<HistoricalOrderObj> getHistoricalOrdersFromEsi(Integer regionId, Integer typeId){
        String endpoint = "/markets/{region_id}/history";
        Map<String, Object> pathParams = new HashMap<>();
        pathParams.put("region_id", regionId);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("type_id", typeId.toString());
        List<HistoricalOrder> historicalOrders = esiClient.get(
                endpoint,
                pathParams,
                queryParams,
                new ParameterizedTypeReference<List<HistoricalOrder>>() {});
        List<HistoricalOrderObj> historicalOrderObjs = historicalOrders.stream()
                .map(this::exchangeToHistoricalOrderObj)
                .peek(historicalOrderObj -> {
                    historicalOrderObj.setRegionId(regionId);
                    historicalOrderObj.setTypeId(typeId);
                })
                .toList();
        return historicalOrderObjs;
    }

    private HistoricalOrderObj exchangeToHistoricalOrderObj(HistoricalOrder historicalOrder) {
        HistoricalOrderObj historicalOrderObj = new HistoricalOrderObj();
        historicalOrderObj.setDate(historicalOrder.getDate());
        historicalOrderObj.setOrderCount(historicalOrder.getOrderCount());
        historicalOrderObj.setVolume(historicalOrder.getVolume());
        historicalOrderObj.setAverage(historicalOrder.getAverage());
        historicalOrderObj.setHighest(historicalOrder.getHighest());
        historicalOrderObj.setLowest(historicalOrder.getLowest());
        return historicalOrderObj;
    }
}