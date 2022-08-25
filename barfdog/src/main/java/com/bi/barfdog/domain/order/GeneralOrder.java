package com.bi.barfdog.domain.order;

import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.orderItem.OrderCancel;
import com.bi.barfdog.domain.orderItem.OrderItem;
import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("general")
@Entity
public class GeneralOrder extends Order{

    private OrderCancel orderCancel;

    @OneToMany(mappedBy = "generalOrder")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @Builder
    public GeneralOrder(Long id, String impUid, String merchantUid, OrderStatus orderStatus, Member member, int orderPrice, int deliveryPrice, int discountTotal, int discountReward, int discountCoupon, int paymentPrice, PaymentMethod paymentMethod, LocalDateTime paymentDate, LocalDateTime orderConfirmDate, boolean isPackage, boolean isBrochure, boolean isAgreePrivacy, Delivery delivery, OrderCancel orderCancel) {
        super(id, impUid, merchantUid, orderStatus, member, orderPrice, deliveryPrice, discountTotal, discountReward, discountCoupon, paymentPrice, paymentMethod, paymentDate, orderConfirmDate, isPackage, isBrochure, isAgreePrivacy, delivery);
        this.orderCancel = orderCancel;
        this.orderItemList = new ArrayList<>();
    }

    public GeneralOrder(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    public void addOrderItemList(OrderItem orderItem) {
        orderItemList.add(orderItem);
    }
}
