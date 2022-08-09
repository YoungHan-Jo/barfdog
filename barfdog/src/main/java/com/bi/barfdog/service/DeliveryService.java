package com.bi.barfdog.service;

import com.bi.barfdog.api.deliveryDto.OrderIdListDto;
import com.bi.barfdog.api.deliveryDto.QueryOrderInfoForDelivery;
import com.bi.barfdog.api.deliveryDto.UpdateDeliveryNumberDto;
import com.bi.barfdog.common.RandomString;
import com.bi.barfdog.config.finalVariable.GoodsFlow;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
    private final OrderItemRepository orderItemRepository;
    private final DogRepository dogRepository;

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
//            if (delivery.getTransUniqueCd() != null && !delivery.getTransUniqueCd().isEmpty()) continue;

            if (delivery.getTransUniqueCd() == null || delivery.getTransUniqueCd().isEmpty()) {
                RandomString rs = new RandomString(15);
                String randomString = rs.nextString();
                delivery.generateTransUniqueCd(randomString);
            }

            List<Order> orderListSameDelivery = orderRepository.findByDelivery(delivery);
            List<QueryOrderInfoForDelivery.OrderItemDto> orderItemDtoList = new ArrayList<>();
            for (Order sameDeliveryOrder : orderListSameDelivery) {

                if (sameDeliveryOrder instanceof GeneralOrder) {
                    List<OrderItem> orderItemList = orderItemRepository.findAllByGeneralOrder((GeneralOrder) sameDeliveryOrder);
                    for (OrderItem orderItem : orderItemList) {
                        String ordNo = orderItem.getId().toString();
                        String ordDate = orderItem.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                        QueryOrderInfoForDelivery.OrderItemDto orderItemDto = QueryOrderInfoForDelivery.OrderItemDto.builder()
                                .uniqueCd(ordNo + "-item")
                                .ordNo(ordNo)
                                .itemName(orderItem.getItem().getName())
                                .itemQty(orderItem.getAmount())
                                .ordDate(ordDate)
                                .build();
                        orderItemDtoList.add(orderItemDto);
                    }
                } else {
                    SubscribeOrder subscribeOrder = (SubscribeOrder) sameDeliveryOrder;
                    String ordNo = subscribeOrder.getId().toString();
                    String ordDate = subscribeOrder.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                    QueryOrderInfoForDelivery.OrderItemDto orderItemDto = QueryOrderInfoForDelivery.OrderItemDto.builder()
                            .uniqueCd(ordNo + "-subs")
                            .ordNo(ordNo)
                            .itemName("구독상품")
                            .itemQty(1)
                            .ordDate(ordDate)
                            .build();
                    orderItemDtoList.add(orderItemDto);
                }
            }

            QueryOrderInfoForDelivery queryOrderInfoForDelivery = QueryOrderInfoForDelivery.builder()
                    .transUniqueCd(delivery.getTransUniqueCd())
                    .sndName(GoodsFlow.SND_NAME)
                    .sndZipCode(GoodsFlow.SND_ZIPCODE)
                    .sndAddr1(GoodsFlow.SND_ADDR1)
                    .sndAddr2(GoodsFlow.SND_ADDR2)
                    .sndTel1(GoodsFlow.SND_TEL)
                    .rcvName(delivery.getRecipient().getName())
                    .rcvZipCode(delivery.getRecipient().getZipcode())
                    .rcvAddr1(delivery.getRecipient().getStreet())
                    .rcvAddr2(delivery.getRecipient().getDetailAddress())
                    .rcvTel1(delivery.getRecipient().getPhone())
                    .mallId(GoodsFlow.MALL_ID)
                    .orderItems(orderItemDtoList)
                    .build();

            responseDto.add(queryOrderInfoForDelivery);
        }
        return responseDto;
    }

    @Transactional
    public void setDeliveryNumber(UpdateDeliveryNumberDto requestDto) {
        List<UpdateDeliveryNumberDto.DeliveryNumberDto> deliveryNumberDtoList = requestDto.getDeliveryNumberDtoList();

        for (UpdateDeliveryNumberDto.DeliveryNumberDto deliveryNumberDto : deliveryNumberDtoList) {
            String transUniqueCd = deliveryNumberDto.getTransUniqueCd();
            Optional<Delivery> optionalDelivery = deliveryRepository.findByTransUniqueCd(transUniqueCd);
            if (!optionalDelivery.isPresent()) continue;
            Delivery delivery = optionalDelivery.get();
            delivery.start(deliveryNumberDto.getDeliveryNumber());

            List<Order> orderList = orderRepository.findByDelivery(delivery);
            for (Order order : orderList) {
                order.startDelivery();
                if (order instanceof GeneralOrder) {
                    List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder((GeneralOrder) order);
                    for (OrderItem orderItem : orderItems) {
                        orderItem.startDelivery();
                    }
                    try {
                        String dogName = getRepresentativeDogName(order.getMember());
                        DirectSendUtils.sendGeneralOrderDeliveryStartAlim(order, dogName, orderItems);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        DirectSendUtils.sendSubscribeOrderDeliveryStartAlim(order);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getRepresentativeDogName(Member member) {
        List<Dog> dogList = dogRepository.findRepresentativeDogByMember(member);
        String dogName = "";
        if (dogList.size() > 0) {
            dogName = dogList.get(0).getName();
        }
        return dogName;
    }









}
