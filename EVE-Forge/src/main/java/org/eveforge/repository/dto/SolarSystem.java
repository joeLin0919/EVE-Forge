package org.eveforge.repository.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SolarSystem {
    /**
     * 星系ID
     */
    @JsonProperty("system_id")
    private Integer systemId;

    /**
     * 星系名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 星系安全状态
     */
    @JsonProperty("security_status")
    private Double securityStatus;

    /**
     * 星系安全等级
     */
    @JsonProperty("security_class")
    private String securityClass;

    /**
     * 所属星座ID
     */
    @JsonProperty("constellation_id")
    private Integer constellationId;

    /**
     * 恒星ID
     */
    @JsonProperty("star_id")
    private Integer starId;

    /**
     * 星门ID列表
     */
    @JsonProperty("stargates")
    private List<Integer> stargates;

    /**
     * 空间站ID列表
     */
    @JsonProperty("stations")
    private List<Integer> stations;

    /**
     * 星系坐标
     */
    @JsonProperty("position")
    private Position position;

    /**
     * 行星列表
     */
    @JsonProperty("planets")
    private List<planet> planets;



    @Data
    public static class Position {

        /**
         * X 坐标
         */
        @JsonProperty("x")
        private Double x;

        /**
         * Y 坐标
         */
        @JsonProperty("y")
        private Double y;

        /**
         * Z 坐标
         */
        @JsonProperty("z")
        private Double z;
    }

    @Data
    public static class planet{

        /**
         * 小行星带ID列表
         */
        @JsonProperty("asteroid_belts")
        private List<Integer> asteroidBelts;

        /**
         * 卫星ID列表
         */
        @JsonProperty("moons")
        private List<Integer> moons;

        /**
         * 行星ID
         */
        @JsonProperty("planet_id")
        private Integer planetId;
    }


}
