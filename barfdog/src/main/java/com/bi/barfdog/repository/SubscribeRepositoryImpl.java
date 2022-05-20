package com.bi.barfdog.repository;

import com.bi.barfdog.api.memberDto.MemberSubscribeAdminDto;
import com.bi.barfdog.api.memberDto.QuerySubscribeAdminDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.domain.dog.QDog.dog;
import static com.bi.barfdog.domain.member.QMember.member;
import static com.bi.barfdog.domain.subscribe.QSubscribe.subscribe;
import static com.bi.barfdog.domain.surveyReport.QSurveyReport.surveyReport;

@RequiredArgsConstructor
@Repository
public class SubscribeRepositoryImpl implements SubscribeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    private final SubscribeRecipeRepository subscribeRecipeRepository;


    @Override
    public Page<MemberSubscribeAdminDto> findSubscribeAdminDtoByMemberId(Long id, Pageable pageable) {
        List<MemberSubscribeAdminDto> result = new ArrayList<>();

        List<QuerySubscribeAdminDto> querySubscribeAdminDtos = queryFactory
                .select(Projections.constructor(QuerySubscribeAdminDto.class,
                        subscribe.id,
                        dog.name,
                        subscribe.createdDate,
                        subscribe.plan,
                        surveyReport.foodAnalysis.oneMealRecommendGram,
                        subscribe.nextPaymentPrice,
                        subscribe.nextDeliveryDate
                ))
                .from(subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .join(dog.surveyReport, surveyReport)
                .where(member.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(subscribe.nextDeliveryDate.desc())
                .fetch();

        for (QuerySubscribeAdminDto querySubscribeAdminDto : querySubscribeAdminDtos) {

            List<String> recipeNames = subscribeRecipeRepository.findRecipeNamesBySubscribeId(querySubscribeAdminDto.getId());

            MemberSubscribeAdminDto memberSubscribeAdminDto = MemberSubscribeAdminDto.builder()
                    .querySubscribeAdminDto(querySubscribeAdminDto)
                    .recipeNames(recipeNames)
                    .build();

            result.add(memberSubscribeAdminDto);
        }

        Long totalCount = queryFactory
                .select(subscribe.count())
                .from(subscribe)
                .join(subscribe.dog, dog)
                .join(dog.member, member)
                .join(dog.surveyReport, surveyReport)
                .where(member.id.eq(id))
                .fetchOne();

        return new PageImpl<>(result,pageable,totalCount);
    }
}
