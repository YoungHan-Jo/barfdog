package com.bi.barfdog.api;

import com.bi.barfdog.api.surveyReportDto.SurveyReportResponseDto;
import com.bi.barfdog.api.surveyReportDto.SurveyResultResponseDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import com.bi.barfdog.repository.SurveyReportRepository;
import com.bi.barfdog.service.SurveyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/surveyReports", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class SurveyReportApiController {

    private final SurveyReportRepository surveyReportRepository;
    private final SurveyReportService surveyReportService;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping("/{id}")
    public ResponseEntity querySurveyReport(@PathVariable Long id) {
        Optional<SurveyReport> optionalSurveyReport = surveyReportRepository.findById(id);
        if (!optionalSurveyReport.isPresent()) {
            return notFound();
        }

        SurveyReportResponseDto responseDto = surveyReportService.getSurveyReportResponseDto(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(SurveyReportApiController.class).slash(id);
        EntityModel<SurveyReportResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.slash("result").withRel("surveyReport_result"),
                profileRootUrlBuilder.slash("index.html#resources-query-surveyReport").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/{id}/result")
    public ResponseEntity querySurveyResult(@PathVariable Long id) {
        Optional<SurveyReport> optionalSurveyReport = surveyReportRepository.findById(id);
        if (!optionalSurveyReport.isPresent()) {
            return notFound();
        }

        SurveyResultResponseDto responseDto = surveyReportService.getSurveyResultResponseDto(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(SurveyReportApiController.class).slash(id).slash("result");

        EntityModel<SurveyResultResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(OrderApiController.class).slash("sheet").slash("subscribe").withRel("query_orderSheet_subscribe"),
                profileRootUrlBuilder.slash("index.html#resources-query-surveyResult").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }



    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<EntityModel<Errors>> conflict(Errors errors) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorsResource(errors));
    }
}
