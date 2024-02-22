package com.example.nyamnyamgood.item.repository;

import java.util.List;

import com.example.nyamnyamgood.customerItem.entity.QCustomerItem;
import com.example.nyamnyamgood.item.entity.Item;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

import static com.example.nyamnyamgood.customerItem.entity.QCustomerItem.customerItem;
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
                storeIdEq(storeId),
                isRemaining()
            )
            .fetch();
    }

    @Override
    public List<Item> showRemainItemListByStoreIdOrderBySell(long storeId) {
        return queryFactory
            .select(item)
            .from(item)
            .where(
                storeIdEq(storeId),
                isRemaining()
            )
            .join(customerItem).on(item.itemId.eq(customerItem.itemId))
            .groupBy(customerItem.itemId)
            .orderBy(customerItem.itemId.desc())
            .fetch();
    }

    private BooleanExpression storeIdEq(long storeId) {
        return item.storeId.eq(storeId);
    }

    private BooleanExpression isRemaining() {
        return item.remained.goe(1);
    }

}
