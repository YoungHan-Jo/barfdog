package com.bi.barfdog.domain.dog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DogProfilePicture {

    private String folder;
    private String filename;

}
