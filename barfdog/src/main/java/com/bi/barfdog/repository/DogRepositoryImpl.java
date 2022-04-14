package com.bi.barfdog.repository;

import com.bi.barfdog.domain.dog.ActivityLevel;
import com.bi.barfdog.domain.dog.DogSize;
import com.bi.barfdog.domain.dog.QDog;
import com.bi.barfdog.domain.dog.SnackCountLevel;
import com.bi.barfdog.domain.member.QMember;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.dog.QDog.dog;
import static com.bi.barfdog.domain.member.QMember.*;


@RequiredArgsConstructor
@Repository
public class DogRepositoryImpl implements DogRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public double findAvgStartAgeMonth() {
        Double result = queryFactory
                .select(dog.startAgeMonth.avg())
                .from(dog)
                .fetchOne();
        return result;
    }

    @Override
    public List<String> findAgeGroup(long monthRange) {
        List<String> results = queryFactory
                .select(new CaseBuilder()
                        .when(dog.startAgeMonth.between(0, monthRange)).then("1")
                        .when(dog.startAgeMonth.between(monthRange + 1, monthRange * 2)).then("2")
                        .when(dog.startAgeMonth.between(monthRange * 2 + 1, monthRange * 3)).then("3")
                        .when(dog.startAgeMonth.between(monthRange * 3 + 1, monthRange * 4)).then("4")
                        .otherwise("5"))
                .from(dog)
                .fetch();
        return results;
    }

    @Override
    public List<String> findWeightGroupByDogSize(DogSize dogSize, double lightestWeight, double weightRange) {
        List<String> results = queryFactory
                .select(new CaseBuilder()
                        .when(dog.weight.between(lightestWeight, lightestWeight + weightRange)).then("1")
                        .when(dog.weight.between(lightestWeight + weightRange + 0.01, lightestWeight + weightRange * 2)).then("2")
                        .when(dog.weight.between(lightestWeight + weightRange * 2 + 0.01, lightestWeight + weightRange * 3)).then("3")
                        .when(dog.weight.between(lightestWeight + weightRange * 2 + 0.01, lightestWeight + weightRange * 4)).then("4")
                        .otherwise("5"))
                .from(dog)
                .where(dog.dogSize.eq(dogSize))
                .fetch();
        return results;
    }

    @Override
    public List<String> findActivityGroupByDogSize(DogSize dogSize) {
        List<String> results = queryFactory
                .select(new CaseBuilder()
                        .when(dog.dogActivity.activityLevel.eq(ActivityLevel.VERY_LITTLE)).then("1")
                        .when(dog.dogActivity.activityLevel.eq(ActivityLevel.LITTLE)).then("2")
                        .when(dog.dogActivity.activityLevel.eq(ActivityLevel.NORMAL)).then("3")
                        .when(dog.dogActivity.activityLevel.eq(ActivityLevel.MUCH)).then("4")
                        .otherwise("5"))
                .from(dog)
                .where(dog.dogSize.eq(dogSize))
                .fetch();

        return results;
    }

    @Override
    public double findAvgTotalWalkingTimeByCity(String city) {
        Double result = queryFactory
                .select(dog.dogActivity.walkingTimePerOneTime.multiply(dog.dogActivity.walkingCountPerWeek).avg())
                .from(dog)
                .join(dog.member, member)
                .where(member.address.city.eq(city))
                .fetchOne();

        return result;
    }

    @Override
    public double findAvgTotalWalkingTimeByAge(double age) {
        Double result = queryFactory
                .select(dog.dogActivity.walkingTimePerOneTime.multiply(dog.dogActivity.walkingCountPerWeek).avg())
                .from(dog)
                .where(dog.startAgeMonth.divide(12).floor().eq((long) age))
                .fetchOne();

        return result;
    }

    @Override
    public double findAvgTotalWalkingTimeByDogSize(DogSize dogSize) {
        Double result = queryFactory
                .select(dog.dogActivity.walkingTimePerOneTime.multiply(dog.dogActivity.walkingCountPerWeek).avg())
                .from(dog)
                .where(dog.dogSize.eq(dogSize))
                .fetchOne();
        return result;
    }

    @Override
    public List<Long> findRanksById(Long id) {
        List<Long> results = queryFactory
                .select(dog.id)
                .from(dog)
                .orderBy((dog.dogActivity.walkingCountPerWeek.multiply(dog.dogActivity.walkingTimePerOneTime)).desc())
                .fetch();

        return results;
    }

    @Override
    public List<String> findSnackGroupByDogSize(DogSize dogSize) {
        List<String> results = queryFactory
                .select(new CaseBuilder()
                        .when(dog.snackCountLevel.eq(SnackCountLevel.LITTLE)).then("1")
                        .when(dog.snackCountLevel.eq(SnackCountLevel.NORMAL)).then("2")
                        .otherwise("3")
                )
                .from(dog)
                .where(dog.dogSize.eq(dogSize))
                .fetch();
        return results;
    }


}
