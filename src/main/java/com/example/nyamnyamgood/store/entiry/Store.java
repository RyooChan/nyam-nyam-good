package com.example.nyamnyamgood.store.entiry;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Store {
    @Id
    private long storeId;

    private String storeName;
    private Type storeType;
}
