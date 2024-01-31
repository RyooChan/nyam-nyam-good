package com.example.nyamnyamgood.customer.entiry;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Customer {
    @Id
    private long customerId;

    private String userName;
    private int point;
}
