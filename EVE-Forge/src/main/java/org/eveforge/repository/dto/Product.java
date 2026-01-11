package org.eveforge.repository.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 物品类型实体类
 * 对应 ESI API: /universe/types/{type_id}/
 */
@Data
public class Product {

    /**
     * 容量 (m³)
     */
    @JsonProperty("capacity")
    private Double capacity;

    /**
     * 物品描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * Dogma 属性列表
     */
    @JsonProperty("dogma_attributes")
    private List<DogmaAttribute> dogmaAttributes;

    /**
     * Dogma 效果列表
     */
    @JsonProperty("dogma_effects")
    private List<DogmaEffect> dogmaEffects;

    /**
     * 图形 ID
     */
    @JsonProperty("graphic_id")
    private Integer graphicId;

    /**
     * 物品组 ID
     */
    @JsonProperty("group_id")
    private Integer groupId;

    /**
     * 图标 ID
     */
    @JsonProperty("icon_id")
    private Integer iconId;

    /**
     * 市场组 ID
     */
    @JsonProperty("market_group_id")
    private Integer marketGroupId;

    /**
     * 质量 (kg)
     */
    @JsonProperty("mass")
    private Double mass;

    /**
     * 物品名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 打包体积 (m³)
     */
    @JsonProperty("packaged_volume")
    private Double packagedVolume;

    /**
     * 份量大小
     */
    @JsonProperty("portion_size")
    private Integer portionSize;

    /**
     * 是否已发布到市场
     */
    @JsonProperty("published")
    private Boolean published;

    /**
     * 半径 (m)
     */
    @JsonProperty("radius")
    private Double radius;

    /**
     * 物品类型 ID
     */
    @JsonProperty("type_id")
    private Integer typeId;

    /**
     * 体积 (m³)
     */
    @JsonProperty("volume")
    private Double volume;

    /**
     * Dogma 属性
     */
    @Data
    public static class DogmaAttribute {
        /**
         * 属性 ID
         */
        @JsonProperty("attribute_id")
        private Integer attributeId;
        
        /**
         * 属性值
         */
        @JsonProperty("value")
        private Double value;
    }

    /**
     * Dogma 效果
     */
    @Data
    public static class DogmaEffect {
        /**
         * 效果 ID
         */
        @JsonProperty("effect_id")
        private Integer effectId;
        
        /**
         * 是否为默认效果
         */
        @JsonProperty("is_default")
        private Boolean isDefault;
    }
}
