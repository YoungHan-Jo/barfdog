package com.bi.barfdog.api;

import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import com.bi.barfdog.repository.DogRepository;
import com.bi.barfdog.repository.RecipeRepository;
import com.bi.barfdog.service.DogService;
import com.bi.barfdog.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/dogs", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class DogApiController extends BaseTimeEntity {

    private final DogService dogService;
    private final DogRepository dogRepository;
    private final RecipeRepository recipeRepository;

    private final CommonValidator commonValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping
    public ResponseEntity createDog(@RequestBody @Valid DogSaveRequestDto requestDto,
                                    Errors errors,
                                    @CurrentUser Member member) {
        if (errors.hasErrors()) return badRequest(errors);
        commonValidator.numberValidatorInString(requestDto.getWeight(),errors);
        commonValidator.numberValidatorInString(requestDto.getWalkingCountPerWeek(),errors);
        commonValidator.numberValidatorInString(requestDto.getWalkingTimePerOneTime(),errors);
        if (errors.hasErrors()) return badRequest(errors);

        Optional<Recipe> optionalRecipe = recipeRepository.findById(requestDto.getRecommendRecipeId());
        if (!optionalRecipe.isPresent()) {
            return notFound();
        }

        dogService.createDogAndSurveyReport(requestDto, member);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(DogApiController.class);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.slash("surveyReport").withRel("query-surveyReport"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-dog").withRel("profile"));

        return ResponseEntity.created(linkTo(DogApiController.class).slash("surveyReport").toUri()).body(representationModel);
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
