package com.bi.barfdog.repository.delivery;

import com.bi.barfdog.api.deliveryDto.QueryDeliveriesDto;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeliveryRepositoryCustom {
    Page<QueryDeliveriesDto> findDeliveriesDto(Member member, Pageable pageable);

    List<Delivery> findPackageDeliveryDto(Member member);
}
