package com.example.nyamnyamgood.customer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long customerId;

    private String customerName;
    private int point;

    public void minusPoint(int point) {
        if (this.point - point < 0) {
            throw new IllegalStateException("소지금보다 비싼 물건을 구매할 수 없습니다.");
        }

        this.point = this.point - point;
    }
}
