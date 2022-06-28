package com.bi.barfdog.repository.surveyReport;

import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyReportRepository extends JpaRepository<SurveyReport, Long>,SurveyReportRepositoryCustom {

}
