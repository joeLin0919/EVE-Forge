package org.eveforge.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.eveforge.repository.po.HistoricalOrderObj;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface HistoricalOrderMapper {

    List<HistoricalOrderObj> getHistoricalOrderByTypeIdAndRegionId(Integer typeId, Integer regionId);

    void insertHistoricalOrders(List<HistoricalOrderObj> historicalOrderObj);

    List<Integer> getAllTypeIds();

    void deleteHistoricalOrdersByDate(Integer typeId, Integer regionId,Timestamp date);

    Timestamp getLatestDate(Integer typeId, Integer regionId);
}
