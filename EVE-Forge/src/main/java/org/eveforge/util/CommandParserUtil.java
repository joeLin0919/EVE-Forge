package org.eveforge.util;

/**
 * 命令解析工具类，提供通用的命令解析功能
 */
public class CommandParserUtil {

    /**
     * 解析物品名称和数量
     * @param itemName 原始物品名称，可能包含 *n 格式
     * @return 包含物品名称和数量的数组，[0]为物品名，[1]为数量
     */
    public static String[] parseItemNameAndQuantity(String itemName) {
        String[] result = new String[2];
        result[0] = itemName; // 默认物品名称
        result[1] = "1";      // 默认数量为1
        
        // 检查是否包含 *n 格式
        if (itemName.contains("*")) {
            int lastStarIndex = itemName.lastIndexOf("*");
            String namePart = itemName.substring(0, lastStarIndex).trim();
            String quantityPart = itemName.substring(lastStarIndex + 1).trim();
            
            // 检查数量部分是否为数字
            if (quantityPart.matches("\\d+")) {
                result[0] = namePart;
                result[1] = quantityPart;
            }
        }
        
        return result;
    }
    
    /**
     * 格式化价格并乘以数量
     * @param price 原始价格
     * @param quantity 数量
     * @return 计算后的价格
     */
    public static double calculateTotalPrice(double price, int quantity) {
        return price * quantity;
    }
}