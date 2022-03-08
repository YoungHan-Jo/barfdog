package com.bi.barfdog.domain.banner;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@Getter
public abstract class Banner {

    @Id @GeneratedValue
    @Column(name = "banner_id")
    private Long id;

    private String name;
    private LocalDateTime createDate;
    private int leakedOrder;

    private String pcUrlLink;
    private String mobileUrlLink;

    @Enumerated(EnumType.STRING)
    private BannerStatus status; // [LEAKED, HIDDEN]

    public Banner(String name, LocalDateTime createDate, int leakedOrder, String pcUrlLink, String mobileUrlLink, BannerStatus status) {
        this.name = name;
        this.createDate = createDate;
        this.leakedOrder = leakedOrder;
        this.pcUrlLink = pcUrlLink;
        this.mobileUrlLink = mobileUrlLink;
        this.status = status;
    }
}
