package com.bi.barfdog.repository;

import com.bi.barfdog.domain.dog.DogSize;

import java.util.List;

public interface DogRepositoryCustom {
    double findAvgStartAgeMonth();

    List<String> findAgeGroup(long monthRange);

    List<String> findWeightGroupByDogSize(DogSize dogSize,double lightestWeight, double weightRange);

    List<String> findActivityGroupByDogSize(DogSize dogSize);

    double findAvgTotalWalkingTimeByCity(String city);

    double findAvgTotalWalkingTimeByAge(double floor);

    double findAvgTotalWalkingTimeByDogSize(DogSize dogSize);

    List<Long> findRanksById(Long id);

    List<String> findSnackGroupByDogSize(DogSize dogSize);

    List<String> findDogNamesByMemberId(Long memberId);
}
