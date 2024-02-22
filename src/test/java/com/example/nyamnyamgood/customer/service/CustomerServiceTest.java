package com.example.nyamnyamgood.customer.service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.nyamnyamgood.customer.entity.Customer;
import com.example.nyamnyamgood.customer.repository.CustomerRepository;
import com.example.nyamnyamgood.customerItem.entity.CustomerItem;
import com.example.nyamnyamgood.customerItem.repository.CustomerItemRepository;
import com.example.nyamnyamgood.item.entity.Item;
import com.example.nyamnyamgood.item.repository.ItemRepository;
import com.example.nyamnyamgood.item.service.ItemService;
import com.example.nyamnyamgood.store.entity.Store;
import com.example.nyamnyamgood.store.entity.StoreType;
import com.example.nyamnyamgood.store.repository.StoreRepository;
import com.example.nyamnyamgood.store.service.StoreService;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerServiceTest {

    @Autowired
    CustomerService customerService;

    @Autowired
    ItemService itemService;

    @Autowired
    StoreService storeService;

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
    @Transactional
    public void 물건_구매_성공_테스트() {
        Store store = this.storeService.saveStore("음식점1", StoreType.KOREAN);
        Item item = this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 10);
        Customer customer = this.customerService.customerSave("맛집헌터", 10000);

        CustomerItem customerItem = this.customerService.buyItem(customer.getCustomerId(), item.getItemId());
        assertThat(customerItem.getCustomerId()).isEqualTo(customer.getCustomerId());
        assertThat(customerItem.getItemId()).isEqualTo(item.getItemId());
    }

    @Test
    public void 물건_구매_실패_테스트() {
        Store store = this.storeService.saveStore("store", StoreType.KOREAN);
        Item item = this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 1);
        Customer c1 = this.customerService.customerSave("c1", 5000);
        Customer c2 = this.customerService.customerSave("c2", 10000);
        Customer c3 = this.customerService.customerSave("c3", 10000);

        {
            IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
                () -> this.customerService.buyItem(c1.getCustomerId(), item.getItemId())
            );
            assertThat(illegalStateException.getMessage()).isEqualTo("소지금보다 비싼 물건을 구매할 수 없습니다.");
        }

        {
            CustomerItem customerItem = this.customerService.buyItem(c2.getCustomerId(), item.getItemId());
            assertThat(customerItem.getCustomerId()).isEqualTo(c2.getCustomerId());
            assertThat(customerItem.getItemId()).isEqualTo(item.getItemId());
        }

        {
            IllegalStateException illegalStateException2 = assertThrows(IllegalStateException.class,
                () -> this.customerService.buyItem(c3.getCustomerId(), item.getItemId())
            );
            assertThat(illegalStateException2.getMessage()).isEqualTo("남은 재고가 없습니다.");
        }
    }

    @Test
    public void 여러_고객이_하나의_물건을_구매한다() throws InterruptedException {
        Store store = this.storeService.saveStore("store", StoreType.KOREAN);
        Item item = this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 12);

        int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i=0; i<threadCount; i++) {
            executorService.submit(() -> {
                try {
                    Customer customer = this.customerService.customerSave("손님", 10000);
                    this.customerService.buyItemWithRedisson(customer.getCustomerId(), item.getItemId());

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        List<CustomerItem> all = this.customerItemRepository.findAll();
        assertThat(all.size()).isEqualTo(12);

    }


    @Test
    public void 하나의_고객이_여러번_물건을_구매한다() throws InterruptedException {
        Store store = this.storeService.saveStore("store", StoreType.KOREAN);
        Item item = this.itemService.itemSave(store.getStoreId(), "비빔밥", 8000, 12);
        Customer customer = this.customerService.customerSave("손님", 45000);

        int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i=0; i<threadCount; i++) {
            executorService.submit(() -> {
                try {
                    this.customerService.buyItemWithRedisson(customer.getCustomerId(), item.getItemId());

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        List<CustomerItem> all = this.customerItemRepository.findAll();
        assertThat(all.size()).isEqualTo(5);
    }
}