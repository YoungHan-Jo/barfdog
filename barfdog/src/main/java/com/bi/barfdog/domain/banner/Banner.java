package com.bi.barfdog.domain.banner;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@AllArgsConstructor
public abstract class Banner extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "banner_id")
    private Long id;

    private String name;

    private String pcLinkUrl; // 배너 클릭 시 이동하는 url , PC
    private String mobileLinkUrl; // 배너 클릭 시 이동하는 url , Mobile

    @Enumerated(EnumType.STRING) // Enum 타입을 사용할 때는 반드시 STRING 타입으로 사용할것
    private BannerStatus status; // 배너 노출 상태 [LEAKED, HIDDEN]

}
