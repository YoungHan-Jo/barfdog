package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.UploadedImageDto;
import com.bi.barfdog.api.dogDto.DogSaveRequestDto;
import com.bi.barfdog.api.dogDto.QueryDogDto;
import com.bi.barfdog.api.dogDto.QueryDogsDto;
import com.bi.barfdog.api.dogDto.UpdateDogPictureDto;
import com.bi.barfdog.api.surveyReportDto.SurveyReportResponseDto;
import com.bi.barfdog.api.surveyReportDto.DogSurveyResultResponseDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.dog.DogPicture;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import com.bi.barfdog.repository.surveyReport.SurveyReportRepository;
import com.bi.barfdog.repository.dog.DogPictureRepository;
import com.bi.barfdog.repository.dog.DogRepository;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.service.DogService;
import com.bi.barfdog.service.SurveyReportService;
import com.bi.barfdog.validator.CommonValidator;
import com.bi.barfdog.validator.DogValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/dogs", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class DogApiController {

    private final DogService dogService;
    private final DogRepository dogRepository;
    private final RecipeRepository recipeRepository;
    private final DogPictureRepository dogPictureRepository;
    private final SurveyReportRepository surveyReportRepository;

    private final SurveyReportService surveyReportService;
    private final CommonValidator commonValidator;
    private final DogValidator dogValidator;

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

        SurveyReport surveyReport = dogService.createDogAndGetSurveyReport(requestDto, member);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(DogApiController.class).withSelfRel());
        WebMvcLinkBuilder querySurveyReportLinkBuilder = linkTo(SurveyReportApiController.class).slash(surveyReport.getId());
        representationModel.add(querySurveyReportLinkBuilder.withRel("query_surveyReport"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-dog").withRel("profile"));

        return ResponseEntity.created(querySurveyReportLinkBuilder.toUri()).body(representationModel);
    }


    @GetMapping
    public ResponseEntity queryDogs(@CurrentUser Member member) {

        List<EntityModel<QueryDogsDto>> entityModelList = new ArrayList<>();

        List<QueryDogsDto> responseDto = dogRepository.findDogsDtoByMember(member);

        for (QueryDogsDto dto : responseDto) {
            EntityModel<QueryDogsDto> entityModel = EntityModel.of(dto,
                    linkTo(DogApiController.class).slash(dto.getId()).slash("picture").withRel("update_picture"),
                    linkTo(DogApiController.class).slash(dto.getId()).slash("representative").withRel("set_representative_dog"),
                    linkTo(DogApiController.class).slash(dto.getId()).withRel("query_dog"),
                    linkTo(DogApiController.class).slash(dto.getId()).slash("surveyReport").withRel("query_surveyReport"),
                    linkTo(DogApiController.class).slash(dto.getId()).withRel("delete_dog")
            );

            entityModelList.add(entityModel);
        }

        CollectionModel<EntityModel<QueryDogsDto>> collectionModel = CollectionModel.of(entityModelList,
                linkTo(DogApiController.class).withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-dogs").withRel("profile")
                );

        return ResponseEntity.ok(collectionModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteDog(@PathVariable Long id) {
        Optional<Dog> optionalDog = dogRepository.findById(id);
        if (!optionalDog.isPresent()) return notFound();
        Dog dog = optionalDog.get();
        if(dog.isRepresentative()) return ResponseEntity.badRequest().build();

        dogService.deleteDog(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(DogApiController.class).withSelfRel());
        representationModel.add(linkTo(DogApiController.class).withRel("query_dogs"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-dog").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PostMapping("/picture/upload")
    public ResponseEntity uploadPicture(@RequestPart MultipartFile file) {
        if(file.isEmpty()) return ResponseEntity.badRequest().build();

        UploadedImageDto responseDto = dogService.uploadPicture(file);

        EntityModel<UploadedImageDto> entityModel = EntityModel.of(responseDto,
                linkTo(DogApiController.class).slash("picture/upload").withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-upload-dogPicture").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/{id}/picture")
    public ResponseEntity updateDogPicture(@CurrentUser Member member,
                                           @PathVariable Long id,
                                           @RequestBody @Valid UpdateDogPictureDto requestDto,
                                           Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Dog> optionalDog = dogRepository.findById(id);
        if (!optionalDog.isPresent()) return notFound();
        Long pictureId = requestDto.getDogPictureId();
        if (pictureId != null) {
            Optional<DogPicture> optionalDogPicture = dogPictureRepository.findById(pictureId);
            if (!optionalDogPicture.isPresent()) return notFound();
            DogPicture dogPicture = optionalDogPicture.get();
            if (dogPicture.getDog() != null) return new ResponseEntity(HttpStatus.CONFLICT);
        }
        dogValidator.validateMyDog(member, id, errors);
        if (errors.hasErrors()) return badRequest(errors);

        dogService.updatePicture(id,requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(DogApiController.class).slash(id).slash("picture").withSelfRel());
        representationModel.add(linkTo(DogApiController.class).withRel("query_dogs"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-dogPicture").withRel("profile"));


        return ResponseEntity.ok(representationModel);
    }

    @PutMapping("/{id}/representative")
    public ResponseEntity updateDogPicture(@CurrentUser Member member,
                                           @PathVariable Long id) {
        Optional<Dog> optionalDog = dogRepository.findById(id);
        if (!optionalDog.isPresent()) return notFound();

        dogService.setRepresentative(member, id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(DogApiController.class).slash(id).slash("representative").withSelfRel());
        representationModel.add(linkTo(DogApiController.class).withRel("query_dogs"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-representative-dog").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryDog(@PathVariable Long id) {
        Optional<Dog> optionalDog = dogRepository.findById(id);
        if(!optionalDog.isPresent()) return notFound();

        QueryDogDto responseDto = dogRepository.findDogDtoByDog(id);

        EntityModel<QueryDogDto> entityModel = EntityModel.of(responseDto,
                linkTo(DogApiController.class).slash(id).withSelfRel(),
                linkTo(DogApiController.class).slash(id).withRel("update_dog"),
                profileRootUrlBuilder.slash("index.html#resources-query-dog").withRel("profile")
        );


        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateDog(@CurrentUser Member member,
                                    @PathVariable Long id,
                                    @RequestBody @Valid DogSaveRequestDto requestDto,
                                    Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Dog> optionalDog = dogRepository.findById(id);
        if(!optionalDog.isPresent()) return notFound();
        dogValidator.validateMyDog(member, id, errors);
        if (errors.hasErrors()) return badRequest(errors);

        dogService.updateDog(member, id, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(DogApiController.class).slash(id).withSelfRel());
        representationModel.add(linkTo(DogApiController.class).slash(id).slash("surveyReport").withRel("query_surveyReport"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-dog").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/{id}/surveyReport")
    public ResponseEntity querySurveyReport(@PathVariable Long id) {
        Optional<Dog> optionalDog = dogRepository.findById(id);
        if (!optionalDog.isPresent()) return notFound();
        Dog dog = optionalDog.get();

        SurveyReport surveyReport = surveyReportRepository.findByDogId(dog.getId());

        SurveyReportResponseDto responseDto = surveyReportService.getSurveyReportResponseDto(surveyReport.getId());

        EntityModel<SurveyReportResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(DogApiController.class).slash(id).slash("surveyReport").withSelfRel(),
                linkTo(DogApiController.class).slash(id).slash("surveyReportResult").withRel("query_surveyReportResult"),
                profileRootUrlBuilder.slash("index.html#resources-query-dog-surveyReport").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/{id}/surveyReportResult")
    public ResponseEntity querySurveyReportResult(@PathVariable Long id) {
        Optional<Dog> optionalDog = dogRepository.findById(id);
        if (!optionalDog.isPresent()) return notFound();
        SurveyReport surveyReport = optionalDog.get().getSurveyReport();
        DogSurveyResultResponseDto responseDto = surveyReportService.getDogSurveyResultResponseDto(surveyReport.getId());

        Long subscribeId = responseDto.getSubscribeId();
        EntityModel<DogSurveyResultResponseDto> entityModel = EntityModel.of(responseDto,
                linkTo(DogApiController.class).slash(id).slash("surveyReportResult").withSelfRel(),
                linkTo(OrderApiController.class).slash("sheet/subscribe").slash(subscribeId).withRel("query-orderSheet-subscribe"),
                linkTo(SubscribeApiController.class).slash(subscribeId).withRel("update_subscribe"),
                profileRootUrlBuilder.slash("index.html#resources-query-dog-surveyReportResult").withRel("profile")
        );

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
