package com.bi.barfdog.domain.dog;

import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class DogProfile {

    @Id @GeneratedValue
    @Column(name = "dog_profile_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id")
    private Dog dog;

    private String folder;
    private String filename;

}
