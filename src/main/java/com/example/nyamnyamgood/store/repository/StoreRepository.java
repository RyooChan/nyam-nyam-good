package com.example.nyamnyamgood.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nyamnyamgood.store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
