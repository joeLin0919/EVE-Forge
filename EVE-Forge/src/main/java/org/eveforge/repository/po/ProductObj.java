package org.eveforge.repository.po;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 物品类型数据库实体类
 */
@Data
public class ProductObj implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 容量 (m³)
     */
    private Double capacity;

    /**
     * 物品描述
     */
    private String description;

    /**
     * 属性 ID
     */
    private Integer attributeId;

    /**
     * 属性值
     */
    private Double value;

    /**
     * 效果 ID
     */
    private Integer effectId;

    /**
     * 是否为默认效果
     */
    private Boolean isDefault;

    /**
     * 图形 ID
     */
    private Integer graphicId;

    /**
     * 物品组 ID
     */
    private Integer groupId;

    /**
     * 图标 ID
     */
    private Integer iconId;

    /**
     * 市场组 ID
     */
    private Integer marketGroupId;

    /**
     * 质量 (kg)
     */
    private Double mass;

    /**
     * 物品名称
     */
    private String name;

    /**
     * 打包体积 (m³)
     */
    private Double packagedVolume;

    /**
     * 份量大小
     */
    private Integer portionSize;

    /**
     * 是否已发布到市场
     */
    private Boolean published;

    /**
     * 半径 (m)
     */
    private Double radius;

    /**
     * 物品类型 ID（主键）
     */
    private Integer typeId;

    /**
     * 体积 (m³)
     */
    private Double volume;
}
