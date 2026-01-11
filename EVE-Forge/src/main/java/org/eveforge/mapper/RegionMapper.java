package org.eveforge.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.eveforge.repository.po.RegionObj;

@Mapper
public interface RegionMapper {
    void insertRegionInfo(RegionObj region);
    Integer selectRegionCountById(Integer regionId);
    RegionObj selectRegionInfoById(Integer regionId);
}
