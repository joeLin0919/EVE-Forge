package org.eveforge.repository.po;


import lombok.Data;



/**
 * 星域数据库实体类
 */
@Data
public class RegionObj {
    /**
     * 星域ID(主键)
     */
    private Integer regionId;
    /**
     * 星域名称
     */
    private String name;
    /**
     * 星域描述
     */
    private String description;
}
