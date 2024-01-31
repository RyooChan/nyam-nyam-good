package com.example.nyamnyamgood.item.entiry;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;

@Entity
public class Item {
    @Id
    private long itemId;
    private int amount;
    private int remained;
    private long storeId;
}
