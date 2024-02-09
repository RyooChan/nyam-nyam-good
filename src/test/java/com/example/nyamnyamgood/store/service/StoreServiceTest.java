package com.example.nyamnyamgood.store.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.nyamnyamgood.store.entity.Store;
import com.example.nyamnyamgood.store.entity.StoreType;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class StoreServiceTest {

    @Autowired
    StoreService storeService;

    @Test
    public void saveStoreTest() {
        Store store = this.storeService.saveStore("테스트", StoreType.KOREAN);
        assertThat(store.getStoreName()).isEqualTo("테스트");
    }
}