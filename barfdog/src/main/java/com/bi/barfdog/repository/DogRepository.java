package com.bi.barfdog.repository;

import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.dog.DogSize;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long>, DogRepositoryCustom {

    List<Dog> findByMember(Member member);


    @Query("select max(d.startAgeMonth) from Dog d")
    int findOldestMonth();

    @Query("select avg(d.weight) from Dog d where d.dogSize = :dogSize")
    double findAvgWeightByDogSize(@Param("dogSize") DogSize dogSize);

    @Query("select max(d.weight) from Dog d where d.dogSize = :dogSize")
    double findFattestWeightByDogSize(@Param("dogSize")DogSize dogSize);

    @Query("select min(d.weight) from Dog d where d.dogSize = :dogSize")
    double findLightestWeightByDogSize(@Param("dogSize") DogSize dogSize);
}
