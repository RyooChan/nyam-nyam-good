package com.example.nyamnyamgood.customer.service;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.nyamnyamgood.config.Utils;
import com.example.nyamnyamgood.item.entity.Item;

@Component
public class CustomerEventListener {

    private CustomerEventListener getCustomerEventListener() {
        return Utils.getBean(CustomerEventListener.class);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void transactionalEventListenerAfterCommit(Item itemEvent) {
        this.getCustomerEventListener().refreshItemCache(itemEvent.getStoreId());
    }

    @CacheEvict(value = "itemCache", key = "#storeId")
    public void refreshItemCache(long storeId) {
    }
}
