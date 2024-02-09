package com.example.nyamnyamgood.item.repository;

import java.util.List;

import com.example.nyamnyamgood.item.entity.Item;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

import static com.example.nyamnyamgood.item.entity.QItem.item;

public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Item> selectItemListByStoreId(long storeId) {
        return queryFactory
            .selectFrom(item)
            .where(
                storeIdEq(storeId)
            )
            .fetch();
    }

    private BooleanExpression storeIdEq(long storeId) {
        return item.storeId.eq(storeId);
    }

}
