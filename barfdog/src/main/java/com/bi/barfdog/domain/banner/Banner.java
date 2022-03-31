package com.bi.barfdog.domain.banner;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@Getter @NoArgsConstructor @Setter
@AllArgsConstructor
public abstract class Banner extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "banner_id")
    private Long id;

    private String name;

    private String pcLinkUrl;
    private String mobileLinkUrl;

    @Enumerated(EnumType.STRING)
    private BannerStatus status; // [LEAKED, HIDDEN]

}
