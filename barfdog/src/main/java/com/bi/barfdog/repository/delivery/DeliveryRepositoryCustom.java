package com.bi.barfdog.repository.delivery;

import com.bi.barfdog.api.deliveryDto.QueryDeliveriesDto;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryRepositoryCustom {
    Page<QueryDeliveriesDto> findDeliveriesDto(Member member, Pageable pageable);
}
