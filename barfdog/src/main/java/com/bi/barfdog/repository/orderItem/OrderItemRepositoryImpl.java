package com.bi.barfdog.repository.orderItem;

import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.orderItem.QOrderItem.*;

@RequiredArgsConstructor
@Repository
public class OrderItemRepositoryImpl implements OrderItemRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public List<OrderItem> findWriteableByMember(Member member) {
        return queryFactory
                .select(orderItem)
                .from(orderItem)
                .where(orderItem.generalOrder.member.eq(member).and(orderItem.writeableReview.isTrue()))
                .fetch()
                ;
    }
}
