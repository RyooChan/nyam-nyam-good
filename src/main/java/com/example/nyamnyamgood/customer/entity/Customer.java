package com.example.nyamnyamgood.customer.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Customer {
    @Id
    private long customerId;

    private String customerName;
    private int point;
}
