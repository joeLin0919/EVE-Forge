package org.eveforge.repository.po;


import lombok.Data;

@Data
public class ConstellationObj {

    /**
     * 星座ID(主键)
     */
    private Integer constellationId;

    /**
     * 星座名称
     */
    private String name;

    /**
     * 所属星域ID
     */
    private Integer regionId;

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
}
