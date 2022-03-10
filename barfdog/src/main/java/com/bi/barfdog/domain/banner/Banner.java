package com.bi.barfdog.domain.banner;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@Getter @NoArgsConstructor
@AllArgsConstructor
public abstract class Banner extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "banner_id")
    private Long id;

    private String name;
    private int leakedOrder;

    @Embedded
    private LinkUrl linkUrl;

    @Enumerated(EnumType.STRING)
    private BannerStatus status; // [LEAKED, HIDDEN]

}
