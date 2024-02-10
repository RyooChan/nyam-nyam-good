package com.example.nyamnyamgood.customerItem.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nyamnyamgood.customerItem.entity.CustomerItem;
import com.example.nyamnyamgood.customerItem.repository.CustomerItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerItemService {
    private final CustomerItemRepository customerItemRepository;

    public CustomerItem customerItemSave(long customerId, long itemId) {
        CustomerItem customerItem = CustomerItem.builder()
            .customerId(customerId)
            .itemId(itemId)
            .build();

        return this.customerItemRepository.save(customerItem);
    }
}
