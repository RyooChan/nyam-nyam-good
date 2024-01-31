package com.example.nyamnyamgood.store.entiry;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Store {
    @Id
    private long storeId;

    private Type storeType;
}
