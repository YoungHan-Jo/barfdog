package com.bi.barfdog.domain.event;

import com.bi.barfdog.api.eventDto.UpdateEventRequestDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder @Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Event extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventStatus status; // [LEAKED, HIDDEN]

    private String title;

    public void update(UpdateEventRequestDto requestDto) {
        this.status = requestDto.getStatus();
        this.title = requestDto.getTitle();
    }
}
