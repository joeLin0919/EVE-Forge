package org.eveforge.plugin;


import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.eveforge.repository.po.HistoricalOrderObj;
import org.eveforge.repository.vo.ProductPriceVo;
import org.eveforge.service.IItemNgramMatchingService;
import org.eveforge.repository.enums.MarketLocationEnum;
import org.eveforge.service.IItemService;
import org.eveforge.service.IMarketService;
import org.eveforge.util.ChartUtil;
import org.eveforge.util.TextMatchingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Base64;

@Component
public class MarketPlugin extends BotPlugin {

    private static final Logger logger = LoggerFactory.getLogger(MarketPlugin.class);
    
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###.##");

    @Autowired
    private IItemNgramMatchingService itemNgramMatchingService;

    @Autowired
    private IMarketService marketService;

    @Autowired
    private IItemService itemService;

    /**
     * 私聊消息处理
     *
     * @param bot       机器人对象
     * @param event     事件对象
     * @return int
     */
    @Override
    public int onPrivateMessage(Bot bot, PrivateMessageEvent event) {

        // 处理以 "ojita" 开头的消息
        if (event.getMessage().startsWith("ojita")) {
            // 提取物品名称（去除 "ojita" 前缀并去除首尾空格）
            String itemName = event.getMessage().substring(5).trim();

            if (!itemName.isEmpty()) {
                try {
                    // 检查是否为物品名*N格式，如果是则不显示趋势图
                    boolean isQuantityQuery = itemName.matches(".*\\*\\d+");
                    
                    List<ProductPriceVo> priceResults = marketService.getProductPricesByName(itemName);
                    
                    if (priceResults.isEmpty()) {
                        String errorMsg = "查询失败：未查到对应物品";
                        logger.info("私聊消息查询失败，用户: {}, 查询物品: {}, 原因: 未找到匹配的物品", event.getUserId(), itemName);
                        bot.sendPrivateMsg(event.getUserId(), errorMsg, false);
                        return MESSAGE_IGNORE;
                    }
                    
                    StringBuilder responseMsg = new StringBuilder();
                    for (ProductPriceVo priceVo : priceResults) {
                        String formattedBuyPrice = DECIMAL_FORMAT.format(priceVo.getBuyPrice()) + " ISK";
                        String formattedSellPrice = DECIMAL_FORMAT.format(priceVo.getSellPrice()) + " ISK";
                        String formattedMiddlePrice = DECIMAL_FORMAT.format(priceVo.getMiddlePrice()) + " ISK";

                        responseMsg.append(String.format("物品: %s\n-出售: %s\n-收购: %s\n-中间: %s\n\n",
                                priceVo.getName(),
                                formattedSellPrice,
                                formattedBuyPrice,
                                formattedMiddlePrice));
                    }
                    
                    // 如果不是物品名*N格式，则生成趋势图并添加到消息中
                    if (!isQuantityQuery) {
                        List<TextMatchingUtil.MatchingResult> matches;
                        if(marketService.isPLEX(itemName)){
                            matches = itemNgramMatchingService.findAllMatches("伊甸币");
                        }else {
                            matches = itemNgramMatchingService.findAllMatches(itemName);
                        }
                        
                        if ((matches.size() <= 3 && !matches.isEmpty())||matches.getFirst().getSimilarity()>0.5) {
                            org.eveforge.util.TextMatchingUtil.MatchingResult firstMatch = matches.get(0);
                            String matchedItemName = firstMatch.getMatchedText();
                            List<Integer> itemIds = itemService.getItemIdByName(matchedItemName);
                            if (!itemIds.isEmpty()) {
                                Integer typeId = itemIds.get(0); // 使用匹配到的物品ID
                                List<HistoricalOrderObj> historicalOrders;
                                if(marketService.isPLEX(matchedItemName)){
                                    historicalOrders = marketService.getHistoricalOrders(MarketLocationEnum.PLEX.getRegionId(), 44992);
                                }else {
                                    historicalOrders = marketService.getHistoricalOrders(MarketLocationEnum.JITA.getRegionId(), typeId); // 使用Jita区域ID
                                }
                                if (!historicalOrders.isEmpty()) {
                                    try {
                                        byte[] chartBytes = ChartUtil.generatePriceTrendChart(historicalOrders, matchedItemName);
                                        String base64Chart = Base64.getEncoder().encodeToString(chartBytes);
                                        String chartMsg = "[CQ:image,file=base64://" + base64Chart + "]";
                                        responseMsg.append("\n").append(chartMsg); // 将图片添加到消息中
                                    } catch (Exception e) {
                                        logger.error("生成价格趋势图失败", e);
                                        responseMsg.append("\n生成价格趋势图失败");
                                    }
                                } else {
                                    responseMsg.append("\n暂无历史价格数据");
                                }
                            } else {
                                responseMsg.append("\n无法获取物品ID，无法生成图表");
                            }
                        } else if (matches.size() > 3) { // 如果匹配列表中个数大于3个时，跳过生成
                            responseMsg.append("\n匹配数量过多，跳过生成趋势图");
                        } else { // 没有匹配的物品
                            responseMsg.append("\n未找到匹配的物品");
                        }
                    }
                    
                    // 发送包含价格信息和图表的完整消息
                    bot.sendPrivateMsg(event.getUserId(), responseMsg.toString(), false);
                } catch (Exception e) {
                    // 发送统一错误消息
                    String errorMsg = "查询失败：未查到对应物品";
                    logger.info("私聊消息查询失败，用户: {}, 查询物品: {}, 原因: {}", event.getUserId(), itemName, e.getMessage());
                    bot.sendPrivateMsg(event.getUserId(), errorMsg, false);
                }
            } else {
                // 如果没有提供物品名称，发送提示信息
                String helpMsg = "请提供要查询的物品名称，例如: ojita 三钛合金";
                bot.sendPrivateMsg(event.getUserId(), helpMsg, false);
            }
        }

        // 返回 MESSAGE_IGNORE 执行 plugin-list 下一个插件，返回 MESSAGE_BLOCK 则不执行下一个插件
        return MESSAGE_IGNORE;
    }

    /**
     * 群消息处理
     *
     * @param bot       机器人对象
     * @param event     事件对象
     * @return int
     */
    @Override
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {


        // 处理以 "ojita" 开头的消息
        if (event.getMessage().startsWith("ojita")) {
            // 提取物品名称（去除 "ojita" 前缀并去除首尾空格）
            String itemName = event.getMessage().substring(5).trim();

            if (!itemName.isEmpty()) {
                try {
                    // 检查是否为物品名*N格式，如果是则不显示趋势图
                    boolean isQuantityQuery = itemName.matches(".*\\*\\d+");
                    
                    List<ProductPriceVo> priceResults = marketService.getProductPricesByName(itemName);
                    
                    if (priceResults.isEmpty()) {
                        String errorMsg = "查询失败：未查到对应物品";
                        logger.info("群聊消息查询失败，群号: {}, 用户: {}, 查询物品: {}, 原因: 未找到匹配的物品", event.getGroupId(), event.getUserId(), itemName);
                        bot.sendGroupMsg(event.getGroupId(), errorMsg, false);
                        return MESSAGE_IGNORE;
                    }
                    
                    StringBuilder responseMsg = new StringBuilder();
                    for (ProductPriceVo priceVo : priceResults) {
                        String formattedBuyPrice = DECIMAL_FORMAT.format(priceVo.getBuyPrice()) + " ISK";
                        String formattedSellPrice = DECIMAL_FORMAT.format(priceVo.getSellPrice()) + " ISK";
                        String formattedMiddlePrice = DECIMAL_FORMAT.format(priceVo.getMiddlePrice()) + " ISK";

                        responseMsg.append(String.format("物品: %s\n-出售: %s\n-收购: %s\n-中间: %s\n\n",
                                priceVo.getName(), 
                                formattedSellPrice,
                                formattedBuyPrice,
                                formattedMiddlePrice));
                    }
                    
                    // 如果不是物品名*N格式，则生成趋势图并添加到消息中
                    if (!isQuantityQuery) {
                        List<TextMatchingUtil.MatchingResult> matches;
                        if(marketService.isPLEX(itemName)){
                            matches = itemNgramMatchingService.findAllMatches("伊甸币");
                        }else {
                            matches = itemNgramMatchingService.findAllMatches(itemName);
                        }
                        
                        if ((matches.size() <= 3 && !matches.isEmpty())||matches.getFirst().getSimilarity()>0.5) { // 如果匹配列表中个数小于等于3个时，取第一个
                            org.eveforge.util.TextMatchingUtil.MatchingResult firstMatch = matches.get(0);
                            String matchedItemName = firstMatch.getMatchedText();
                            List<Integer> itemIds = itemService.getItemIdByName(matchedItemName);
                            if (!itemIds.isEmpty()) {
                                Integer typeId = itemIds.get(0); // 使用匹配到的物品ID
                                List<HistoricalOrderObj> historicalOrders;
                                if(marketService.isPLEX(matchedItemName)){
                                    historicalOrders = marketService.getHistoricalOrders(MarketLocationEnum.PLEX.getRegionId(), 44992);
                                }else {
                                    historicalOrders = marketService.getHistoricalOrders(MarketLocationEnum.JITA.getRegionId(), typeId); // 使用Jita区域ID
                                }
                                if (!historicalOrders.isEmpty()) {
                                    try {
                                        byte[] chartBytes = ChartUtil.generatePriceTrendChart(historicalOrders, matchedItemName);
                                        String base64Chart = Base64.getEncoder().encodeToString(chartBytes);
                                        String chartMsg = "[CQ:image,file=base64://" + base64Chart + "]";
                                        responseMsg.append("\n").append(chartMsg); // 将图片添加到消息中
                                    } catch (Exception e) {
                                        logger.error("生成价格趋势图失败", e);
                                        responseMsg.append("\n生成价格趋势图失败");
                                    }
                                } else {
                                    responseMsg.append("\n暂无历史价格数据");
                                }
                            } else {
                                responseMsg.append("\n无法获取物品ID，无法生成图表");
                            }
                        } else if (matches.size() > 3) { // 如果匹配列表中个数大于3个时，跳过生成
                            logger.info("\n匹配数量过多，跳过生成趋势图");
                        } else { // 没有匹配的物品
                            responseMsg.append("\n未找到匹配的物品");
                        }
                    }
                    
                    // 发送包含价格信息和图表的完整消息
                    bot.sendGroupMsg(event.getGroupId(), responseMsg.toString(), false);
                } catch (Exception e) {
                    // 发送统一错误消息
                    String errorMsg = "查询失败：未查到对应物品";
                    logger.info("群聊消息查询失败，群号: {}, 用户: {}, 查询物品: {}, 原因: {}", event.getGroupId(), event.getUserId(), itemName, e.getMessage());
                    bot.sendGroupMsg(event.getGroupId(), errorMsg, false);
                }
            } else {
                // 如果没有提供物品名称，发送提示信息
                String helpMsg = "请提供要查询的物品名称，例如: ojita 三钛合金";
                bot.sendGroupMsg(event.getGroupId(), helpMsg, false);
            }
        }

        // 返回 MESSAGE_IGNORE 执行 plugin-list 下一个插件，返回 MESSAGE_BLOCK 则不执行下一个插件
        return MESSAGE_IGNORE;
    }
}