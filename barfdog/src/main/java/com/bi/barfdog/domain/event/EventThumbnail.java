package com.bi.barfdog.domain.event;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class EventThumbnail extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "event_thumbnail_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "event_id")
    private Event event; // 썸네일: 이벤트 일대일관계

    private String folder;
    private String filename;

    public void setEvent(Event event) {
        this.event = event;
    }

}
