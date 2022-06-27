package com.bi.barfdog.repository.dog;

import com.bi.barfdog.domain.dog.DogPicture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogPictureRepository extends JpaRepository<DogPicture, Long> {
}
