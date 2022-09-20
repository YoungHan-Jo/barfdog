package com.bi.barfdog.domain.dog;

import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class DogPicture { // 강아지 프로필 사진

    @Id @GeneratedValue
    @Column(name = "dog_picture_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id")
    private Dog dog; // 강아지:강아지 사진 일대일

    private String folder;
    private String filename;

    public void setDog(Dog dog) {
        this.dog = dog;
    }
}
