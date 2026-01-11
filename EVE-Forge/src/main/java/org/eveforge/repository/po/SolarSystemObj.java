package org.eveforge.repository.po;


import lombok.Data;

@Data
public class SolarSystemObj {

    /**
     * 星系ID(主键)
     */
    private Integer systemId;

    /**
     * 星系名称
     */
    private String name;

    /**
     * 所属星座ID
     */
    private Integer constellationId;

    /**
     * x坐标
     */
    private Double x_coordinate;

    /**
     * y坐标
     */
    private Double y_coordinate;

    /**
     * z坐标
     */
    private Double z_coordinate;

    /**
     * 星系安全状态
     */
    private Double securityStatus;

    /**
     * 恒星ID
     */
    private Integer starId;

    /**
     * 星系安全等级
     */
    private String securityClass;
}
