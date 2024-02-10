package com.example.nyamnyamgood.customer.service;

import java.awt.Point;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nyamnyamgood.customer.entity.Customer;
import com.example.nyamnyamgood.customer.repository.CustomerRepository;
import com.example.nyamnyamgood.customerItem.entity.CustomerItem;
import com.example.nyamnyamgood.customerItem.service.CustomerItemService;
import com.example.nyamnyamgood.item.entity.Item;
import com.example.nyamnyamgood.item.service.ItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ItemService itemService;
    private final CustomerItemService customerItemService;

    @Transactional
    public Customer customerSave(String customerName, int point) {
        Customer customer = Customer.builder()
            .customerName(customerName)
            .point(point)
            .build();

        return this.customerRepository.save(customer);
    }

    public Customer minusPoint(long customerId, Item item) {
        Customer customer = this.customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalStateException("해당하는 유저가 없습니다."));

        int pointAfterBuy = customer.getPoint() - item.getPrice();

        if (pointAfterBuy < 0) {
            throw new IllegalStateException("소지금보다 비싼 물건을 구매할 수 없습니다.");
        }

        customer.setPoint(pointAfterBuy);
        return customer;
    }

    @Transactional
    public CustomerItem buyItem(long customerId, long itemId) {
        Item item = this.itemService.minusRemained(itemId);
        Customer customer = this.minusPoint(customerId, item);
        CustomerItem customerItem = this.customerItemService.customerItemSave(customer.getCustomerId(), item.getItemId());
        return customerItem;
    }


}
