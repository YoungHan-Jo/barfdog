package com.bi.barfdog.domain.event;

import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class EventImage extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "event_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    private int leakOrder;

    private String folder;
    private String filename;

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setLeakOrder(int leakOrder) {
        this.leakOrder = leakOrder;
    }
}
