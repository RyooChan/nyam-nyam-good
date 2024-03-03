package com.example.nyamnyamgood.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.nyamnyamgood.customer.entity.Customer;
import com.example.nyamnyamgood.customer.repository.CustomerRepository;
import com.example.nyamnyamgood.customer.service.CustomerService;
import com.example.nyamnyamgood.customerItem.repository.CustomerItemRepository;
import com.example.nyamnyamgood.item.entity.Item;
import com.example.nyamnyamgood.item.repository.ItemRepository;
import com.example.nyamnyamgood.store.entity.Store;
import com.example.nyamnyamgood.store.entity.StoreType;
import com.example.nyamnyamgood.store.repository.StoreRepository;
import com.example.nyamnyamgood.store.service.StoreService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    StoreService storeService;

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerItemRepository customerItemRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    StoreRepository storeRepository;

    @AfterEach
    void afterTest() {
        customerRepository.deleteAll();
        itemRepository.deleteAll();
        storeRepository.deleteAll();
        customerItemRepository.deleteAll();
    }


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
            item -> assertThat(itemList.contains(item)).isTrue()
        );

        itemNotRemainingList.forEach(
            item -> assertThat(!itemList.contains(item)).isTrue()
        );

    }

    @Test
    public void 여러_유저가_보는_경우() {
        Store store = this.storeService.saveStore("음식점1", StoreType.KOREAN);
        this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 10);

        // 동일성 테스트
        List<Item> itemsWithoutCache = this.itemService.showRemainItemListByStoreId(store.getStoreId());
        List<Item> itemsWithCache = this.itemService.showRemainItemListByStoreIdWithCache(store.getStoreId());

        for (int i=0; i<itemsWithoutCache.size(); i++) {
            assertThat(itemsWithoutCache.get(i).getItemId()).isEqualTo(itemsWithCache.get(i).getItemId());
        }

        // 데이터베이스 찔러서 가져오기
        long startTime = System.currentTimeMillis();

        for (int i=0; i<50000; i++) {
            this.itemService.showRemainItemListByStoreId(store.getStoreId());
        }
        long stopTime = System.currentTimeMillis();

        long timeGapForDatabase = (stopTime - startTime);

        // 캐시를 통해 가져오기
        startTime = System.currentTimeMillis();

        for (int i=0; i<50000; i++) {
            this.itemService.showRemainItemListByStoreIdWithCache(store.getStoreId());
        }
        stopTime = System.currentTimeMillis();

        long timeGapForCache = (stopTime - startTime);

        // 확인
        assertThat(timeGapForDatabase).isGreaterThan(timeGapForCache);
        System.out.println("데이터베이스 : " + timeGapForDatabase);
        System.out.println("캐시 : " + timeGapForCache);
    }

    @Test
    public void 데이터_등록시_갱신_테스트() {
        Store store = this.storeService.saveStore("음식점1", StoreType.KOREAN);
        this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 10);

        List<Item> itemsWithCacheFirst = this.itemService.showRemainItemListByStoreIdWithCache(store.getStoreId());

        this.itemService.itemSave(store.getStoreId(), "비빔국수", 8500, 10);
        List<Item> itemsWithCacheSecond = this.itemService.showRemainItemListByStoreIdWithCache(store.getStoreId());

        assertThat(itemsWithCacheFirst).isNotEqualTo(itemsWithCacheSecond);
        assertThat(itemsWithCacheFirst.size()).isLessThan(itemsWithCacheSecond.size());
    }

    @Test
    public void 재고_없을때_안보이는_테스트() {
        Store store = this.storeService.saveStore("음식점1", StoreType.KOREAN);
        Item item = this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 1);

        List<Item> itemsWithCacheFirst = this.itemService.showRemainItemListByStoreIdWithCache(store.getStoreId());

        Customer customer = this.customerService.customerSave("밥매니아", 30000);
        this.customerService.buyItem(customer.getCustomerId(), item.getItemId());

        List<Item> itemsWithCacheSecond = this.itemService.showRemainItemListByStoreIdWithCache(store.getStoreId());

        assertThat(itemsWithCacheFirst).isNotEqualTo(itemsWithCacheSecond);
        assertThat(itemsWithCacheFirst.size()).isGreaterThan(itemsWithCacheSecond.size());
    }

    @Test
    public void 많이_팔린_순서_정렬() {
        Store store = this.storeService.saveStore("음식점", StoreType.KOREAN);
        Store dummyStore = this.storeService.saveStore("미사용음식점", StoreType.KOREAN);
        Store dummyStore2 = this.storeService.saveStore("미사용음식점2", StoreType.KOREAN);
        List<Item> itemArrayList = new ArrayList<>();
        List<Item> dummyItemArrayList = new ArrayList<>();
        List<Item> dummyItemArrayList2 = new ArrayList<>();

        for(int i=0; i<100; i++) {
            itemArrayList.add(this.itemService.itemSave(store.getStoreId(), "테스트데이터" + i, 1000, 900));
            dummyItemArrayList.add(this.itemService.itemSave(dummyStore.getStoreId(), "더미데이터" + i, 1000, 900));
            dummyItemArrayList2.add(this.itemService.itemSave(dummyStore2.getStoreId(), "더미데이터" + i, 1000, 900));
        }

        Customer customer = this.customerService.customerSave("TestUser", Integer.MAX_VALUE);

        for (int i=0; i<100; i++) {
            for(int j=0; j<=i; j++) {
                this.customerService.buyItem(customer.getCustomerId(), itemArrayList.get(i).getItemId());
                this.customerService.buyItem(customer.getCustomerId(), dummyItemArrayList.get(i).getItemId());
                this.customerService.buyItem(customer.getCustomerId(), dummyItemArrayList2.get(i).getItemId());
            }
        }

        itemArrayList = itemArrayList.reversed();

        long startTime = System.currentTimeMillis();
        List<Item> items = this.itemService.showRemainItemListByStoreIdOrderBySell(store.getStoreId());
        long endTime = System.currentTimeMillis();

        for (int i=0; i<100; i++) {
            assertThat(itemArrayList.get(i).getItemName()).isEqualTo(items.get(i).getItemName());
        }

    }

}