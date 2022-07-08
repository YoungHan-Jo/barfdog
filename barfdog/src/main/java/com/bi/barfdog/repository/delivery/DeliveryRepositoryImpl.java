package com.bi.barfdog.repository.delivery;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom{

    private final JPAQueryFactory queryFactory;
}
