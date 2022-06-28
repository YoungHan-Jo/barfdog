package com.bi.barfdog.repository.surveyReport;

import com.bi.barfdog.domain.surveyReport.SurveyReport;

public interface SurveyReportRepositoryCustom {
    SurveyReport findByDogId(Long id);
}
