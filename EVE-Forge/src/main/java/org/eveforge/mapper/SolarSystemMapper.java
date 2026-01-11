package org.eveforge.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.eveforge.repository.po.SolarSystemObj;

@Mapper
public interface SolarSystemMapper {
    void insertSolarSystemInfo(SolarSystemObj solarSystem);
    Integer selectSolarSystemCountById(Integer systemId);
    SolarSystemObj selectSolarSystemInfoById(Integer systemId);
}
