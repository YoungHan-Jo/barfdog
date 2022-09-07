package com.bi.barfdog.repository.dog;

import com.bi.barfdog.domain.dog.QDogPicture;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.dog.QDogPicture.*;

@RequiredArgsConstructor
@Repository
public class DogPictureRepositoryImpl implements DogPictureRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findFilename() {
        return queryFactory
                .select(dogPicture.filename)
                .from(dogPicture)
                .fetch()
                ;
    }
}
