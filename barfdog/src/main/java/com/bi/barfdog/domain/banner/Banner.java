package com.bi.barfdog.domain.banner;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Banner {

    @Id @GeneratedValue
    @Column(name = "banner_id")
    private Long id;

    private String name;
    private LocalDateTime createDate;
    private int leakedOrder;

    @Enumerated(EnumType.STRING)
    private BannerStatus status; // [LEAKED, HIDDEN]
}
