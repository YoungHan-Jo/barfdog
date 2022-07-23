package com.bi.barfdog.repository.dog;

import com.bi.barfdog.api.dogDto.QueryDogDto;
import com.bi.barfdog.api.dogDto.QueryDogsDto;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.dog.DogSize;
import com.bi.barfdog.domain.member.Member;

import java.util.List;
import java.util.Optional;

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

    void updateAllDogRepresentativeFalse(Member member);

    QueryDogDto findDogDtoByDog(Long id);

    List<QueryDogsDto> findDogsDtoByMember(Member member);

    List<Dog> findRepresentativeDogByMember(Member member);
}
