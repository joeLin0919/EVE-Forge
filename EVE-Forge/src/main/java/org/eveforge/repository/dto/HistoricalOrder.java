package org.eveforge.repository.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class HistoricalOrder {

    /**
     * 平均价格
     */
    @JsonProperty("average")
    private Double average;

    /**
     * 最高价格
     */
    @JsonProperty("highest")
    private Double highest;

    /**
     * 最低价格
     */
    @JsonProperty("lowest")
    private Double lowest;

    /**
     * 订单数量
     */
    @JsonProperty("order_count")
    private Integer orderCount;

    /**
     * 订单总量
     */
    @JsonProperty("volume")
    private Long volume;

    /**
     * 订单时间
     */
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Timestamp date;
}
