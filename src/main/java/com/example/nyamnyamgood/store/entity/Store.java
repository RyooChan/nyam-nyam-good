package com.example.nyamnyamgood.store.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Store {
    @Id
    private long storeId;

    private String storeName;
    private StoreType storeType;
}
