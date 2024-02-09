package com.example.nyamnyamgood.item.service;

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

}