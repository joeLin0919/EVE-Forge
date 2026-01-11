package org.eveforge.repository.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 星座实体类
 */
@Data
public class Constellation {

    /**
     * 星座ID
     */
    @JsonProperty("constellation_id")
    private Integer constellationId;

    /**
     * 星座名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 所属星域ID
     */
    @JsonProperty("region_id")
    private Integer regionId;

    /**
     * 下属星系列表
     */
    @JsonProperty("systems")
    private List<Integer> systems;

    /**
     * 星座坐标
     */
    @JsonProperty("position")
    private Position position;

    /**
     * 星座坐标
     */
    @Data
    public static class Position {

        /**
         * 星座 X 坐标
         */
        @JsonProperty("x")
        private Double x;

        /**
         * 星座 Y 坐标
         */
        @JsonProperty("y")
        private Double y;

        /**
         * 星座 Z 坐标
         */
        @JsonProperty("z")
        private Double z;
    }


}
