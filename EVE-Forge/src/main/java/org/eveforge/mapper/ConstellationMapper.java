package org.eveforge.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.eveforge.repository.po.ConstellationObj;

@Mapper
public interface ConstellationMapper {

    void insertConstellationInfo(ConstellationObj constellationObj);

    Integer selectConstellationCountById(Integer constellationId);
}
