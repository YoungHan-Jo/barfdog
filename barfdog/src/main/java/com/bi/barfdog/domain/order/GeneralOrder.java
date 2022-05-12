package com.bi.barfdog.domain.order;

import com.bi.barfdog.domain.orderItem.OrderItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("general")
@Entity
public class GeneralOrder extends Order{

    @OneToMany(mappedBy = "generalOrder")
    private List<OrderItem> orderItemList = new ArrayList<>();
}
