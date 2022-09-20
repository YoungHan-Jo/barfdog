package com.bi.barfdog.repository.order;

import com.bi.barfdog.api.barfDto.AdminDashBoardRequestDto;
import com.bi.barfdog.api.barfDto.AdminDashBoardResponseDto;
import com.bi.barfdog.api.orderDto.*;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {
    Page<QueryAdminOrdersDto> findAdminOrdersDto(Pageable pageable, OrderAdminCond cond);

    QueryAdminGeneralOrderDto findAdminGeneralOrderDto(Long id);

    QueryAdminSubscribeOrderDto findAdminSubscribeOrderDto(Long id);

    Page<QuerySubscribeOrdersDto> findSubscribeOrdersDto(Member member, Pageable pageable);

    QuerySubscribeOrderDto findSubscribeOrderDto(Long id);

    Page<QueryGeneralOrdersDto> findGeneralOrdersDto(Member member, Pageable pageable);

    QueryGeneralOrderDto findGeneralOrderDto(Long id);

    AdminDashBoardResponseDto findAdminDashBoard(AdminDashBoardRequestDto requestDto);

    Page<QueryAdminCancelRequestDto> findAdminCancelRequestDto(Pageable pageable, OrderAdminCond cond);

    List<Order> findDeliveryDoneForAutoConfirm();

    Long findOrderingCountByMember(Member member);

    Long findReservedOrderCount(Member member);
}
