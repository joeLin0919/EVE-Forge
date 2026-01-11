package org.eveforge.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.eveforge.repository.po.ProductObj;

import java.util.List;


@Mapper
public interface ProductMapper {
    /**
     * 插入物品信息
     * @param productObj
     */
    void insertProductInfo(ProductObj productObj);

    Integer selectProductCountById(@Param("typeId") Integer typeId);

    List<Integer> getProductIdByName(@Param("name") String name);

    String getProductNameById(@Param("typeId") Integer typeId);

    List<String> getAllProductNames();

    void alterPLEXName();

    Integer getGroupIdByName(@Param("name") String name);
}
