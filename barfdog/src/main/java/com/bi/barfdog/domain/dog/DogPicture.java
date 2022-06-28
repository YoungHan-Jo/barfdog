package com.bi.barfdog.domain.dog;

import lombok.*;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @Getter
@Entity
public class DogPicture {

    @Id @GeneratedValue
    @Column(name = "dog_picture_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id")
    private Dog dog;

    private String folder;
    private String filename;

    public void setDog(Dog dog) {
        this.dog = dog;
    }
}
