package com.bi.barfdog.domain.order;

import com.bi.barfdog.domain.delivery.Delivery;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.orderItem.OrderItem;
import lombok.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("general")
@Entity
public class GeneralOrder extends Order{

    @OneToMany(mappedBy = "generalOrder")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @Builder
    public GeneralOrder(Long id, String impUid, String merchantUid, OrderStatus orderStatus, Member member, int orderPrice, int deliveryPrice, int discountTotal, int discountReward, int discountCoupon, int paymentPrice, int saveReward, boolean isSavedReward, PaymentMethod paymentMethod, boolean isPackage, Delivery delivery, List<OrderItem> orderItemList) {
        super(id, impUid, merchantUid, orderStatus, member, orderPrice, deliveryPrice, discountTotal, discountReward, discountCoupon, paymentPrice, saveReward, isSavedReward, paymentMethod, isPackage, delivery);
        this.orderItemList = orderItemList;
    }

    public GeneralOrder(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
