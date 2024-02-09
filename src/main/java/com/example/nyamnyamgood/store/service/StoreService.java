package com.example.nyamnyamgood.store.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nyamnyamgood.store.entity.Store;
import com.example.nyamnyamgood.store.entity.StoreType;
import com.example.nyamnyamgood.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public Store saveStore(String storeName, StoreType storeType) {
        Store store = Store.builder()
            .storeName(storeName)
            .storeType(storeType)
            .build();

        return this.storeRepository.save(store);
    }
}
