package com.example.nyamnyamgood.item.repository;

import java.util.List;

import com.example.nyamnyamgood.item.entity.Item;

public interface ItemRepositoryCustom {
    List<Item> selectItemListByStoreId(long storeId);

    List<Item> showRemainItemListByStoreIdOrderBySell(long storeId);
}
