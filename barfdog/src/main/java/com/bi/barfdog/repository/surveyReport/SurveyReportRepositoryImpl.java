package com.bi.barfdog.repository.surveyReport;

import com.bi.barfdog.domain.surveyReport.QSurveyReport;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.bi.barfdog.domain.surveyReport.QSurveyReport.*;

@RequiredArgsConstructor
@Repository
public class SurveyReportRepositoryImpl implements SurveyReportRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public SurveyReport findByDogId(Long id) {
        return queryFactory
                .select(surveyReport)
                .from(surveyReport)
                .where(surveyReport.dog.id.eq(id))
                .fetchOne()
                ;

    }
}
