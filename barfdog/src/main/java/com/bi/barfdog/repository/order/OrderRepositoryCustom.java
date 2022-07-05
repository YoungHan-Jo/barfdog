package com.bi.barfdog.repository.order;

import com.bi.barfdog.api.orderDto.OrderAdminCond;
import com.bi.barfdog.api.orderDto.QueryAdminGeneralOrderDto;
import com.bi.barfdog.api.orderDto.QueryAdminOrdersDto;
import com.bi.barfdog.api.orderDto.QueryAdminSubscribeOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    Page<QueryAdminOrdersDto> findAdminOrdersDto(Pageable pageable, OrderAdminCond cond);

    QueryAdminGeneralOrderDto findAdminGeneralOrderDto(Long id);

    QueryAdminSubscribeOrderDto findAdminSubscribeOrderDto(Long id);
}
