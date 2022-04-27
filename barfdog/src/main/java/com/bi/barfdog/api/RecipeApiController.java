package com.bi.barfdog.api;

import com.bi.barfdog.api.recipeDto.RecipeListResponseDto;
import com.bi.barfdog.api.recipeDto.RecipeResponseDto;
import com.bi.barfdog.api.recipeDto.RecipeRequestDto;
import com.bi.barfdog.api.recipeDto.RecipeSurveyResponseDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import com.bi.barfdog.repository.RecipeRepository;
import com.bi.barfdog.service.RecipeService;
import com.bi.barfdog.validator.CommonValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
@RequestMapping(value = "/api/recipes", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class RecipeApiController extends BaseTimeEntity {

    private final RecipeRepository recipeRepository;

    private final RecipeService recipeService;

    private final ModelMapper modelMapper;

    private final CommonValidator commonValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @PostMapping
    public ResponseEntity createRecipe(
                    @RequestPart @Valid RecipeRequestDto requestDto, Errors errors,
                    @RequestPart MultipartFile file1,
                    @RequestPart(required = false) MultipartFile file2) {
        if (errors.hasErrors()) return badRequest(errors);
        commonValidator.validateFiles(errors, file1, file2);
        if (errors.hasErrors()) return badRequest(errors);

        validateNotNumber(requestDto, errors);
        if (errors.hasErrors()) return badRequest(errors);

        recipeService.save(requestDto, file1, file2);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RecipeApiController.class);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(selfLinkBuilder.slash("admin").withRel("query_recipes"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-recipe").withRel("profile"));

        return ResponseEntity.created(selfLinkBuilder.toUri()).body(representationModel);
    }



    @GetMapping
    public ResponseEntity queryRecipes() {
        List<EntityModel<RecipeListResponseDto>> entityModelList= new ArrayList<>();
        List<Recipe> recipeList = recipeRepository.findByStatus(RecipeStatus.ACTIVE);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RecipeApiController.class);

        for (Recipe recipe : recipeList) {
            RecipeListResponseDto responseDto = modelMapper.map(recipe, RecipeListResponseDto.class);
            responseDto.setModifiedDate(recipe.getModifiedDate().toLocalDate());

            EntityModel<RecipeListResponseDto> entityModel = EntityModel.of(responseDto,
                    selfLinkBuilder.slash(recipe.getId()).withRel("update_recipe"),
                    selfLinkBuilder.slash(recipe.getId()).slash("inactive").withRel("inactive_recipe")
            );
            entityModelList.add(entityModel);
        }

        CollectionModel<EntityModel<RecipeListResponseDto>> collectionModel = CollectionModel.of(entityModelList,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("create_recipe"),
                profileRootUrlBuilder.slash("index.html#resources-query-recipes").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("{id}")
    public ResponseEntity queryRecipe(@PathVariable Long id) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        if (!optionalRecipe.isPresent()) {
            return notFound();
        }

        Recipe recipe = optionalRecipe.get();

        RecipeResponseDto responseDto = modelMapper.map(recipe, RecipeResponseDto.class);
        responseDto.setThumbnailUri(recipe);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RecipeApiController.class).slash(id);

        EntityModel<RecipeResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                selfLinkBuilder.withRel("update_recipe"),
                profileRootUrlBuilder.slash("index.html#resources-query-recipe").withRel("profile")
                );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/{id}")
    public ResponseEntity updateRecipe(
                    @PathVariable Long id,
                    @RequestPart @Valid RecipeRequestDto requestDto, Errors errors,
                    @RequestPart(required = false) MultipartFile file1,
                    @RequestPart(required = false) MultipartFile file2) {
        if (errors.hasErrors()) return badRequest(errors);
        validateNotNumber(requestDto, errors);
        if (errors.hasErrors()) return badRequest(errors);

        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        if(!optionalRecipe.isPresent()) return ResponseEntity.notFound().build();

        recipeService.updateRecipe(id, requestDto, file1, file2);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RecipeApiController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(
                selfLinkBuilder.withSelfRel(),
                linkTo(RecipeApiController.class).withRel("query_recipes"),
                profileRootUrlBuilder.slash("index.html#resources-update-recipe").withRel("profile")
                );

        return ResponseEntity.ok(representationModel);
    }

    @PutMapping("{id}/inactive")
    public ResponseEntity inactiveRecipe(@PathVariable Long id) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        if (!optionalRecipe.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        recipeService.inactiveRecipe(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RecipeApiController.class).slash(id).slash("inactive");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(
                selfLinkBuilder.withSelfRel(),
                linkTo(RecipeApiController.class).withRel("query_recipes"),
                profileRootUrlBuilder.slash("index.html#resources-inactive-recipe").withRel("profile")
        );

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/ingredients")
    public ResponseEntity queryIngredients() {
        List<String> ingredients = recipeService.getIngredients();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RecipeApiController.class).slash("ingredients");

        CollectionModel<String> collectionModel = CollectionModel.of(ingredients,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-ingredients").withRel("profile")
        );


        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/survey")
    public ResponseEntity queryRecipesForSurvey() {

        List<RecipeSurveyResponseDto> responseDtoList = recipeService.getRecipesForSurvey();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RecipeApiController.class).slash("survey");

        CollectionModel<RecipeSurveyResponseDto> collectionModel = CollectionModel.of(responseDtoList,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-recipesForSurvey").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }



    private void validateNotNumber(RecipeRequestDto requestDto, Errors errors) {
        commonValidator.numberValidatorInString(requestDto.getGramPerKcal(), errors);
        commonValidator.numberValidatorInString(requestDto.getPricePerGram(), errors);
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }


}
