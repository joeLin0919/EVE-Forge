package org.eveforge.repository.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class ProductPriceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物品名称
     */
    private String name;

    /**
     * 购买价格
     */
    private Double buyPrice;

    /**
     * 出售价格
     */
    private Double sellPrice;

    /**
     * 中间价格
     */
    private Double middlePrice;
}
