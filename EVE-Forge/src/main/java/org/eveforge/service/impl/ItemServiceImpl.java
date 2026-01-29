package org.eveforge.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eveforge.mapper.ProductMapper;
import org.eveforge.repository.dto.Product;
import org.eveforge.repository.po.ProductObj;
import org.eveforge.service.IItemService;
import org.eveforge.util.EsiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ESI API 业务服务层
 */
@Service
@Slf4j
public class ItemServiceImpl implements IItemService {

    private final EsiClient esiClient;

    @Autowired
    public ItemServiceImpl(EsiClient esiClient) {
        this.esiClient = esiClient;
    }

    @Autowired
    private ProductMapper productMapper;


    @Override
    public void initProductInfo() {
        log.info("开始初始化物品信息");
        String idEndPoint = "/universe/types";
        String InfoEndPoint = "/universe/types/{type_id}";
        Integer page=1;
        while(true){
            log.info("正在获取物品ID页: {}", page);
            Map<String,String> queryParam = new HashMap<>();
            queryParam.put("page",page.toString());
            String idResponse;
            try {
                idResponse = esiClient.getJson(idEndPoint,queryParam);
            } catch (Exception e) {
                // 捕获404错误，表示已到达最后一页
                if (e.getMessage() != null && e.getMessage().contains("404 Not Found")) {
                    log.info("已获取所有的物品ID页，退出循环");
                    break;
                } else {
                    log.error("获取物品ID页失败: {}", e.getMessage());
                    throw e; // 如果不是404错误，重新抛出异常
                }
            }
            // 分割字符串获得物品ID
            List<Integer> idList = Arrays.stream(idResponse.substring(1, idResponse.length() - 1).split(","))
                    .map(s -> s.trim())
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .toList();
            Integer lastIdCount = productMapper.selectProductCountById(idList.getLast());
            if(lastIdCount != null && lastIdCount != 0){
                page++;
                continue;
            }
            for (Integer id : idList) {
                Map<String, Object> pathParam = new HashMap<>();
                pathParam.put("type_id", id.toString());
                Product product = esiClient.get(InfoEndPoint, pathParam, null, Product.class);
                ProductObj productObj = exchangeToProductObj(product);
                try {
                    productMapper.insertProductInfo(productObj);
                } catch (Exception e) {
                    log.error("插入物品信息失败: {}", e.getMessage());
                }
            }
            page++;
        }
        productMapper.alterPLEXName();
    }

    @Override
    @Async
    @Scheduled(cron = "0 0 7 * * ?")
    public void updateProductInfo() {
        log.info("开始全量更新物品信息");
        String idEndPoint = "/universe/types";
        String InfoEndPoint = "/universe/types/{type_id}";
        Integer page = 1;
        while(true){
            log.info("正在获取物品ID页: {}", page);
            Map<String,String> queryParam = new HashMap<>();
            queryParam.put("page",page.toString());
            String idResponse;
            try {
                idResponse = esiClient.getJson(idEndPoint,queryParam);
            } catch (Exception e) {
                // 捕获404错误，表示已到达最后一页
                if (e.getMessage() != null && e.getMessage().contains("404 Not Found")) {
                    log.info("已获取所有的物品ID页，退出循环");
                    break;
                } else {
                    log.error("获取物品ID页失败: {}", e.getMessage());
                    throw e; // 如果不是404错误，重新抛出异常
                }
            }
            // 分割字符串获得物品ID
            List<Integer> idList = Arrays.stream(idResponse.substring(1, idResponse.length() - 1).split(","))
                    .map(s -> s.trim())
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .toList();
            
            for (Integer id : idList) {
                Map<String, Object> pathParam = new HashMap<>();
                pathParam.put("type_id", id.toString());
                Product product = esiClient.get(InfoEndPoint, pathParam, null, Product.class);
                ProductObj productObj = exchangeToProductObj(product);
                Integer productCount = productMapper.selectProductCountById(id);
                if(productCount != null && productCount != 0){
                    continue;
                }
                try {
                    productMapper.insertProductInfo(productObj);
                    log.info("成功更新物品信息: {}", productObj.getName());
                } catch (Exception e) {
                    log.error("更新物品信息失败: {}", e.getMessage());
                }
            }
            page++;
        }
    }

    @Override
    public List<Integer> getItemIdByName(String name) {
        List<Integer> itemIdList = productMapper.getProductIdByName(name);
        return itemIdList;
    }



    @Override
    public String getItemNameById(Integer itemId) {
        return productMapper.getProductNameById(itemId);
    }

    @Override
    public Integer getGroupIdById(Integer itemId) {
        return productMapper.getGroupIdById(itemId);
    }

    private ProductObj exchangeToProductObj(Product product) {
        ProductObj productObj = new ProductObj();
        
        if (product.getCapacity() != null) {
            productObj.setCapacity(product.getCapacity());
        }
        
        if (product.getDescription() != null) {
            productObj.setDescription(product.getDescription());
        }
        
        if (product.getDogmaAttributes() != null && !product.getDogmaAttributes().isEmpty()) {
            if(product.getDogmaAttributes().getFirst().getAttributeId() != null){
                productObj.setAttributeId(product.getDogmaAttributes().getFirst().getAttributeId());
            }
            if(product.getDogmaAttributes().getFirst().getValue() != null){
                productObj.setValue(product.getDogmaAttributes().getFirst().getValue());
            }
        }
        
        if (product.getDogmaEffects() != null && !product.getDogmaEffects().isEmpty()) {
            if(product.getDogmaEffects().getFirst().getEffectId() != null){
                productObj.setEffectId(product.getDogmaEffects().getFirst().getEffectId());
            }
            if(product.getDogmaEffects().getFirst().getIsDefault() != null){
                productObj.setIsDefault(product.getDogmaEffects().getFirst().getIsDefault());
            }
        }
        
        if (product.getGraphicId() != null) {
            productObj.setGraphicId(product.getGraphicId());
        }
        
        if (product.getGroupId() != null) {
            productObj.setGroupId(product.getGroupId());
        }
        
        if (product.getIconId() != null) {
            productObj.setIconId(product.getIconId());
        }
        
        if (product.getMarketGroupId() != null) {
            productObj.setMarketGroupId(product.getMarketGroupId());
        }
        
        if (product.getMass() != null) {
            productObj.setMass(product.getMass());
        }
        
        if (product.getName() != null) {
            productObj.setName(product.getName());
        }
        
        if (product.getPackagedVolume() != null) {
            productObj.setPackagedVolume(product.getPackagedVolume());
        }
        
        if (product.getPortionSize() != null) {
            productObj.setPortionSize(product.getPortionSize());
        }
        
        if (product.getPublished() != null) {
            productObj.setPublished(product.getPublished());
        }
        
        if (product.getRadius() != null) {
            productObj.setRadius(product.getRadius());
        }
        
        if (product.getTypeId() != null) {
            productObj.setTypeId(product.getTypeId());
        }
        
        if (product.getVolume() != null) {
            productObj.setVolume(product.getVolume());
        }
        
        return productObj;
    }
}


