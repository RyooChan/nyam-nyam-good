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
        Store store = this.storeService.saveStore("음식점1", StoreType.KOREAN);
        Store store2 = this.storeService.saveStore("음식점2", StoreType.KOREAN);
        Item item1 = this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 10);
        Item item2 = this.itemService.itemSave(store.getStoreId(), "육회", 18000, 10);
        Item item3 = this.itemService.itemSave(store.getStoreId(), "잡채", 10000, 0);
        Item item4 = this.itemService.itemSave(store.getStoreId(), "불고기", 10000, 0);
        ArrayList<Item> itemArrayList = new ArrayList<>();
        itemArrayList.add(item1);
        itemArrayList.add(item2);
        for(int i=0; i<30; i++) {
            itemArrayList.add(this.itemService.itemSave(store.getStoreId(), "테스트데이터", 10000, 10));
        }
        for(int i=0; i<3000; i++) {
            this.itemService.itemSave(store2.getStoreId(), "테스트데이터", 10000, 10);
        }
        ArrayList<Item> itemNotRemainingList = new ArrayList<>();
        itemNotRemainingList.add(item3);
        itemNotRemainingList.add(item4);

        List<Item> itemList = this.itemService.showRemainItemListByStoreId(store.getStoreId());

        itemArrayList.forEach(
            item -> assertThat(itemList.contains(item))
        );

        itemNotRemainingList.forEach(
            item -> assertThat(!itemList.contains(item))
        );

    }

    @Test
    public void 여러_유저가_보는_경우() {

        long startTime = System.currentTimeMillis();

        Store store = this.storeService.saveStore("음식점1", StoreType.KOREAN);
        this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 10);

        for (int i=0; i<50000; i++) {
            this.itemService.showRemainItemListByStoreId(store.getStoreId());
        }

        long stopTime = System.currentTimeMillis();

        long timeGapForDatabase = (stopTime - startTime);

        startTime = System.currentTimeMillis();

        for (int i=0; i<50000; i++) {
            this.itemService.showRemainItemListByStoreIdWithCache(store.getStoreId());
        }

        stopTime = System.currentTimeMillis();

        long timeGapForCache = (stopTime - startTime);

        assertThat(timeGapForDatabase).isGreaterThan(timeGapForCache);
        System.out.println("데이터베이스 : " + timeGapForDatabase);
        System.out.println("캐시 : " + timeGapForCache);

    }
}