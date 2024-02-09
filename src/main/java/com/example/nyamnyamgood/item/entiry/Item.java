package com.example.nyamnyamgood.item.entiry;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    @Id
    private long itemId;
    private int amount;
    private int remained;
    private long storeId;
}
