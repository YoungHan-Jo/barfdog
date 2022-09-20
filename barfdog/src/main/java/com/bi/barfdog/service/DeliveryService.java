package com.bi.barfdog.service;

import com.bi.barfdog.api.deliveryDto.OrderIdListDto;
import com.bi.barfdog.api.deliveryDto.QueryOrderInfoForDelivery;
import com.bi.barfdog.api.deliveryDto.UpdateDeliveryNumberDto;
import com.bi.barfdog.common.RandomString;
import com.bi.barfdog.config.finalVariable.GoodsFlow;
import com.bi.barfdog.directsend.DirectSendUtils;
import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.delivery.DeliveryStatus;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.order.GeneralOrder;
import com.bi.barfdog.domain.order.Order;
import com.bi.barfdog.domain.order.OrderStatus;
import com.bi.barfdog.domain.order.SubscribeOrder;
import com.bi.barfdog.domain.orderItem.OrderItem;
import com.bi.barfdog.domain.reward.Reward;
import com.bi.barfdog.domain.reward.RewardName;
import com.bi.barfdog.domain.reward.RewardStatus;
import com.bi.barfdog.domain.reward.RewardType;
import com.bi.barfdog.goodsFlow.CheckTraceResultRequestDto;
import com.bi.barfdog.goodsFlow.GoodsFlowUtils;
import com.bi.barfdog.goodsFlow.TraceResultResponseDto;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import com.bi.barfdog.repository.reward.RewardRepository;
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
    private final RewardRepository rewardRepository;

    @Transactional
    public List<QueryOrderInfoForDelivery> queryInfoForGoodsFlow(OrderIdListDto requestDto) {
        List<Long> idList = requestDto.getOrderIdList();
        List<Long> orderIdList = idList.stream().distinct().collect(Collectors.toList());

        List<QueryOrderInfoForDelivery> responseDto = new ArrayList<>();

        List<String> transUniqueCdList = new ArrayList<>();

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

            if (transUniqueCdList.contains(delivery.getTransUniqueCd())) continue;

            transUniqueCdList.add(delivery.getTransUniqueCd());


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
                    .request(delivery.getRequest())
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
        String dogName = "강아지";
        if (dogList.size() > 0) {
            dogName = dogList.get(0).getName();
        }
        return dogName;
    }


    @Transactional
    public void deliveryDoneScheduler() {

        TraceResultResponseDto traceResults = GoodsFlowUtils.getTraceResults();

        TraceResultResponseDto.InnerData data = traceResults.getData();
        if (data == null) return;

        List<TraceResultResponseDto.InnerData.InnerItem> items = data.getItems();
        if (items.size() == 0) return;

        List<CheckTraceResultRequestDto> checkTraceResultRequestDtoList = new ArrayList<>();

        for (TraceResultResponseDto.InnerData.InnerItem item : items) {
            String dlvStatCode = item.getDlvStatCode();
            if (dlvStatCode.equals("70")) {
                String transUniqueCd = item.getTransUniqueCd();
                Optional<Delivery> optionalDelivery = deliveryRepository.findByTransUniqueCd(transUniqueCd);
                if (!optionalDelivery.isPresent()) continue;

                Delivery delivery = optionalDelivery.get();
                isDeliveryDone(checkTraceResultRequestDtoList, item, delivery);
                isDeliveryStart(checkTraceResultRequestDtoList, item, delivery);

                saveReward(delivery);
            }
        }

        // 결과보고 수신확인 api
        try {
            GoodsFlowUtils.checkTraceResults(checkTraceResultRequestDtoList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveReward(Delivery delivery) {
        List<Order> orderList = orderRepository.findByDelivery(delivery);
        for (Order order : orderList) {
            if (order instanceof SubscribeOrder) {
                SubscribeOrder subscribeOrder = (SubscribeOrder) order;
                Member member = subscribeOrder.getMember();
                int saveReward = subscribeOrder.getSaveReward();

                subscribeOrder.giveExpectedRewards();
                member.chargeReward(saveReward);
                saveSubscribeOrderRewardHistory(subscribeOrder, member);

            }
        }
    }

    private void saveSubscribeOrderRewardHistory(SubscribeOrder subscribeOrder, Member member) {
        Reward reward = Reward.builder()
                .member(member)
                .name(RewardName.CONFIRM_ORDER)
                .rewardType(RewardType.ORDER)
                .rewardStatus(RewardStatus.SAVED)
                .tradeReward(subscribeOrder.getSaveReward())
                .build();
        rewardRepository.save(reward);
    }

    private void isDeliveryStart(List<CheckTraceResultRequestDto> checkTraceResultRequestDtoList, TraceResultResponseDto.InnerData.InnerItem item, Delivery delivery) {
        if (delivery.getStatus() == DeliveryStatus.DELIVERY_START) {
            addCheckTraceResultRequestDto(checkTraceResultRequestDtoList, item);
            delivery.deliveryDone();
            sendDeliveryDoneAlimTalk(delivery);
        }
    }

    private void isDeliveryDone(List<CheckTraceResultRequestDto> checkTraceResultRequestDtoList, TraceResultResponseDto.InnerData.InnerItem item, Delivery delivery) {
        if (delivery.getStatus() == DeliveryStatus.DELIVERY_DONE) {
            addCheckTraceResultRequestDto(checkTraceResultRequestDtoList, item);
        }
    }

    private void sendDeliveryDoneAlimTalk(Delivery delivery) {
        // 배송완료 알림톡
        List<Order> orderList = orderRepository.findByDelivery(delivery);
        for (Order order : orderList) {
            Member member = order.getMember();
            isGeneralOrder(order, member);
            isSubscribeOrder(order);
        }
    }

    private void isSubscribeOrder(Order order) {
        if (order instanceof SubscribeOrder) {
            SubscribeOrder subscribeOrder = (SubscribeOrder) order;

            String dogName = subscribeOrder.getSubscribe().getDog().getName();

            try {
                DirectSendUtils.sendDeliveryDoneAlim(subscribeOrder,dogName, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void isGeneralOrder(Order order, Member member) {
        if (order instanceof GeneralOrder) {
            GeneralOrder generalOrder = (GeneralOrder) order;

            String dogName = getRepresentativeDogName(member);

            List<OrderItem> orderItems = orderItemRepository.findAllByGeneralOrder(generalOrder);

            try {
                DirectSendUtils.sendDeliveryDoneAlim(generalOrder,dogName,orderItems);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addCheckTraceResultRequestDto(List<CheckTraceResultRequestDto> requestDtoList, TraceResultResponseDto.InnerData.InnerItem item) {
        CheckTraceResultRequestDto requestDto = CheckTraceResultRequestDto.builder()
                .uniqueCd(item.getUniqueCd())
                .seq(item.getSeq())
                .build();
        requestDtoList.add(requestDto);
    }

}
