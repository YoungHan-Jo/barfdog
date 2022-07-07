package com.bi.barfdog.repository.order;

import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    Page<QueryAdminOrdersDto> findAdminOrdersDto(Pageable pageable, OrderAdminCond cond);

    QueryAdminGeneralOrderDto findAdminGeneralOrderDto(Long id);

    QueryAdminSubscribeOrderDto findAdminSubscribeOrderDto(Long id);

    Page<QuerySubscribeOrdersDto> findSubscribeOrdersDto(Member member, Pageable pageable);
}
