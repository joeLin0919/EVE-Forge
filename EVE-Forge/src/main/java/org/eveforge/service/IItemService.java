package org.eveforge.service;


import java.util.List;

public interface IItemService {
    void initProductInfo();

    void updateProductInfo();

    List<Integer> getItemIdByName(String name);

    String getItemNameById(Integer itemId);

    Integer getGroupIdByName(String name);
}
