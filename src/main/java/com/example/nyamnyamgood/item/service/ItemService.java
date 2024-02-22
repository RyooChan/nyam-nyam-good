package com.example.nyamnyamgood.item.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.nyamnyamgood.item.entity.Item;
import com.example.nyamnyamgood.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

import static org.springframework.transaction.annotation.Propagation.NESTED;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    @CacheEvict(value = "itemCache", key = "#storeId")
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

    @Cacheable(value = "itemCache", key = "#storeId")
    @Transactional(readOnly = true)
    public List<Item> showRemainItemListByStoreIdWithCache(long storeId) {
        return this.itemRepository.selectItemListByStoreId(storeId);
    }

    public Item minusRemained(long itemId) {
        Item item = this.itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalStateException("해당하는 물품이 없습니다."));

        if (item.getRemained() == 0) {
            throw new IllegalStateException("남은 재고가 없습니다.");
        }

        item.setRemained(item.getRemained() - 1);
        return item;
    }

}
