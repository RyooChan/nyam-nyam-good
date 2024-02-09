package com.example.nyamnyamgood.item.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.nyamnyamgood.item.entity.Item;
import com.example.nyamnyamgood.store.entity.Store;
import com.example.nyamnyamgood.store.entity.StoreType;
import com.example.nyamnyamgood.store.service.StoreService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    StoreService storeService;

    @Test
    public void 아이템_저장_케이스() {
        Store store = this.storeService.saveStore("테스트", StoreType.KOREAN);
        Item item = this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 10);

        assertThat(item.getItemName()).isEqualTo("비빔밥");
    }

    @Test
    public void 아이템_존재_여부_테스트() {
        Store store = this.storeService.saveStore("테스트", StoreType.KOREAN);
        Item item1 = this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 10);
        Item item2 = this.itemService.itemSave(store.getStoreId(), "육회", 18000, 10);
        ArrayList<Item> itemArrayList = new ArrayList<>();
        itemArrayList.add(item1);
        itemArrayList.add(item2);

        List<Item> itemList = this.itemService.showRemainItemListByStoreId(store.getStoreId());

        itemArrayList.forEach(
            item -> assertThat(itemList.contains(item))
        );

    }
}