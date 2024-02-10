package com.example.nyamnyamgood.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nyamnyamgood.customer.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
