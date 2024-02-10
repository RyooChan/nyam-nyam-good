package com.example.nyamnyamgood.customerItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nyamnyamgood.customerItem.entity.CustomerItem;

@Repository
public interface CustomerItemRepository extends JpaRepository<CustomerItem, Long> {
}
