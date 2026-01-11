package org.eveforge.repository.po;


import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class HistoricalOrderObj implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 平均价格
     */
    private Double average;

    /**
     * 最高价格
     */
    private Double highest;

    /**
     * 最低价格
     */
    private Double lowest;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 订单总量
     */
    private Long volume;

    /**
     * 订单时间
     */
    private Timestamp date;

    /**
     * 订单物品类型ID
     */
    private Integer typeId;

    /**
     * 订单所在星域ID
     */
    private Integer RegionId;
}
