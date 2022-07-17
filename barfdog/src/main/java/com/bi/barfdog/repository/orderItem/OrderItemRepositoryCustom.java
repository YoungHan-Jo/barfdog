package com.bi.barfdog.repository.orderItem;

import com.bi.barfdog.api.orderDto.QueryAdminOrderItemDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.orderItem.OrderItem;

import java.util.List;

public interface OrderItemRepositoryCustom {
    List<OrderItem> findWriteableByMember(Member member);

    QueryAdminOrderItemDto findAdminOrderItemDto(Long id);
}
