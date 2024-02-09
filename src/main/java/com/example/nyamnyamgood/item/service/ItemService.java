package com.example.nyamnyamgood.item.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nyamnyamgood.item.entity.Item;
import com.example.nyamnyamgood.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public Item itemSave(long storeId, String itemName, int price, int remained) {
        Item item = Item.builder()
            .storeId(storeId)
            .itemName(itemName)
            .price(price)
            .remained(remained)
            .build();

        return itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<Item> showRemainItemListByStoreId(long storeId) {
        return this.itemRepository.selectItemListByStoreId(storeId);
    }


}
