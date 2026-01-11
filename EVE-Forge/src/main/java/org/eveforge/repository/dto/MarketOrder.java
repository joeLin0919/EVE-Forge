package org.eveforge.repository.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class MarketOrder {

    /**
     * 订单ID
     */
    @JsonProperty("order_id")
    private Long orderId;

    /**
     * 订单持续时间
     */
    @JsonProperty("duration")
    private Integer duration;

    /**
     * 是否为购买订单
     */
    @JsonProperty("is_buy_order")
    private Boolean isBuyOrder;

    /**
     * 订单发布时间
     */
    @JsonProperty("issued")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Timestamp issued;

    /**
     * 订单发布地ID
     */
    @JsonProperty("location_id")
    private Long locationId;

    /**
     * 订单最小成交量
     */
    @JsonProperty("min_volume")
    private Integer minVolume;

    /**
     * 订单价格
     */
    @JsonProperty("price")
    private Double price;

    /**
     * 订单范围
     */
    @JsonProperty("range")
    private String range;

    /**
     * 订单所在星系ID
     */
    @JsonProperty("system_id")
    private Integer systemId;

    /**
     * 订单物品类型ID
     */
    @JsonProperty("type_id")
    private Integer typeId;

    /**
     * 订单剩余数量
     */
    @JsonProperty("volume_remain")
    private Integer volumeRemain;

    /**
     * 订单总量
     */
    @JsonProperty("volume_total")
    private Integer volumeTotal;
}
