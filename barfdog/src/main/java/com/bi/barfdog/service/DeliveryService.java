package com.bi.barfdog.service;

import com.bi.barfdog.api.deliveryDto.OrderIdListDto;
import com.bi.barfdog.api.deliveryDto.QueryOrderInfoForDelivery;
import com.bi.barfdog.common.RandomString;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;


    @Transactional
    public List<QueryOrderInfoForDelivery> queryInfoForGoodsFlow(OrderIdListDto requestDto) {
        List<Long> idList = requestDto.getOrderIdList();
        List<Long> orderIdList = idList.stream().distinct().collect(Collectors.toList());

        List<QueryOrderInfoForDelivery> responseDto = new ArrayList<>();

        for (Long orderId : orderIdList) {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (!optionalOrder.isPresent()) continue;
            Order order = optionalOrder.get();
            if (order.getOrderStatus() == OrderStatus.DELIVERY_START) continue;

            Delivery delivery = order.getDelivery();
            RandomString rs = new RandomString(15);
            String randomString = rs.nextString();
            delivery.start(randomString);
            List<Order> orderListSameDelivery = orderRepository.findByDelivery(delivery);
            List<QueryOrderInfoForDelivery.OrderItemDto> orderItemDtoList = new ArrayList<>();
            for (Order sameDeliveryOrder : orderListSameDelivery) {
                sameDeliveryOrder.startDelivery();


                QueryOrderInfoForDelivery.OrderItemDto orderItemDto = QueryOrderInfoForDelivery.OrderItemDto.builder()
                        .build();

                orderItemDtoList.add(orderItemDto);
            }

            QueryOrderInfoForDelivery queryOrderInfoForDelivery = QueryOrderInfoForDelivery.builder()
                    .transUniqueCd(delivery.getTransUniqueCd())
                    .sndName()
                    .sndZipCode()
                    .sndAddr1()
                    .sndAddr2()
                    .sndTel1()
                    .rcvName()
                    .rcvZipCode()
                    .rcvAddr1()
                    .rcvAddr2()
                    .rcvTel1()
                    .mallId()
                    .orderItemDtoList(orderItemDtoList)
                    .build();

            responseDto.add(queryOrderInfoForDelivery);
        }
        return responseDto;
    }
}
