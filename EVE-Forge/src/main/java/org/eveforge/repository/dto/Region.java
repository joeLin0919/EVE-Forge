package org.eveforge.repository.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 星域实体类
 */
@Data
public class Region {

    /**
     * 星座ID列表
     */
    @JsonProperty("constellations")
    private List<Integer> constellations;

    /**
     * 星域描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 星域名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 星域ID
     */
    @JsonProperty("region_id")
    private Integer regionId;

}
