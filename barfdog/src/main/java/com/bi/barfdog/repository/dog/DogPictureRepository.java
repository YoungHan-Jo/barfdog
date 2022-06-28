package com.bi.barfdog.repository.dog;

import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.dog.DogPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogPictureRepository extends JpaRepository<DogPicture, Long> {
    void deleteAllByDog(Dog dog);

    List<DogPicture> findByDog(Dog dog);
}
