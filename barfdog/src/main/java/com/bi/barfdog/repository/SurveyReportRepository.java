package com.bi.barfdog.repository;

import com.bi.barfdog.domain.surveyReport.SurveyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyReportRepository extends JpaRepository<SurveyReport, Long> {

}
