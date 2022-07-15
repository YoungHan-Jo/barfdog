package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.surveyReportDto.SurveyReportResponseDto;
import com.bi.barfdog.api.surveyReportDto.SurveyResultRecipeDto;
import com.bi.barfdog.api.surveyReportDto.DogSurveyResultResponseDto;
import com.bi.barfdog.api.surveyReportDto.SurveyResultResponseDto;
import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.RecipeStatus;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import com.bi.barfdog.domain.surveyReport.SurveyReport;
import com.bi.barfdog.repository.recipe.RecipeRepository;
import com.bi.barfdog.repository.subscribeRecipe.SubscribeRecipeRepository;
import com.bi.barfdog.repository.surveyReport.SurveyReportRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SurveyReportService {

    private final SurveyReportRepository surveyReportRepository;
    private final RecipeRepository recipeRepository;
    private final ModelMapper modelMapper;
    private final SubscribeRecipeRepository subscribeRecipeRepository;

    public SurveyReportResponseDto getSurveyReportResponseDto(Long id) {
        SurveyReport surveyReport = surveyReportRepository.findById(id).get();

        SurveyReportResponseDto responseDto = modelMapper.map(surveyReport, SurveyReportResponseDto.class);
        responseDto.setLastSurveyDate(surveyReport.getModifiedDate().toLocalDate());
        responseDto.setMyDogName(surveyReport.getDog().getName());

        return responseDto;
    }


    public DogSurveyResultResponseDto getDogSurveyResultResponseDto(Long id) {
        SurveyReport surveyReport = surveyReportRepository.findById(id).get();

        Dog dog = surveyReport.getDog();
        Recipe recommendRecipe = dog.getRecommendRecipe();

        String filename = recommendRecipe.getThumbnailImage().getFilename1();

        String url = MvcUriComponentsBuilder.fromMethodName(InfoController.class,
                "displayByCategory", "recipes",filename).build().toString();
        
        List<SurveyResultRecipeDto> recipeDtoList = new ArrayList<>();

        List<Recipe> recipeList = recipeRepository.findByStatus(RecipeStatus.ACTIVE);

        for (Recipe recipe : recipeList) {
            String imgUrl = MvcUriComponentsBuilder.fromMethodName(InfoController.class,
                    "displayByCategory", "recipes",recipe.getThumbnailImage().getFilename2()).build().toString();

            SurveyResultRecipeDto recipeDto = SurveyResultRecipeDto.builder()
                    .id(recipe.getId())
                    .name(recipe.getName())
                    .description(recipe.getDescription())
                    .pricePerGram(recipe.getPricePerGram())
                    .gramPerKcal(recipe.getGramPerKcal())
                    .inStock(recipe.isInStock())
                    .imgUrl(imgUrl)
                    .build();
            recipeDtoList.add(recipeDto);
        }

        Subscribe subscribe = dog.getSubscribe();
        List<SubscribeRecipe> subscribeRecipes = subscribeRecipeRepository.findBySubscribe(subscribe);
        String recipeName = subscribeRecipes.get(0).getRecipe().getName();
        if (subscribeRecipes.size() > 1) {
            recipeName += "," + subscribeRecipes.get(1).getRecipe().getName();
        }

        DogSurveyResultResponseDto responseDto = DogSurveyResultResponseDto.builder()
                .dogId(dog.getId())
                .dogName(dog.getName())
                .subscribeId(subscribe.getId())
                .subscribeStatus(subscribe.getStatus())
                .recommendRecipeId(recommendRecipe.getId())
                .recommendRecipeName(recommendRecipe.getName())
                .recommendRecipeDescription(recommendRecipe.getDescription())
                .recommendRecipeImgUrl(url)
                .uiNameKorean(recommendRecipe.getUiNameKorean())
                .uiNameEnglish(recommendRecipe.getUiNameEnglish())
                .foodAnalysis(surveyReport.getFoodAnalysis())
                .recipeDtoList(recipeDtoList)
                .plan(subscribe.getPlan())
                .recipeName(recipeName)
                .oneMealRecommendGram(subscribe.getDog().getSurveyReport().getFoodAnalysis().getOneMealRecommendGram())
                .nextPaymentDate(subscribe.getNextPaymentDate())
                .nextPaymentPrice(subscribe.getNextPaymentPrice())
                .nextDeliveryDate(subscribe.getNextDeliveryDate())
                .build();

        return responseDto;
    }

    public SurveyResultResponseDto getSurveyResultResponseDto(Long id) {
        SurveyReport surveyReport = surveyReportRepository.findById(id).get();

        Dog dog = surveyReport.getDog();
        Recipe recommendRecipe = dog.getRecommendRecipe();

        String filename = recommendRecipe.getThumbnailImage().getFilename1();

        String url = MvcUriComponentsBuilder.fromMethodName(InfoController.class,
                "displayByCategory", "recipes",filename).build().toString();

        List<SurveyResultRecipeDto> recipeDtoList = new ArrayList<>();

        List<Recipe> recipeList = recipeRepository.findByStatus(RecipeStatus.ACTIVE);

        for (Recipe recipe : recipeList) {
            String imgUrl = MvcUriComponentsBuilder.fromMethodName(InfoController.class,
                    "displayByCategory", "recipes",recipe.getThumbnailImage().getFilename2()).build().toString();

            SurveyResultRecipeDto recipeDto = SurveyResultRecipeDto.builder()
                    .id(recipe.getId())
                    .name(recipe.getName())
                    .description(recipe.getDescription())
                    .pricePerGram(recipe.getPricePerGram())
                    .gramPerKcal(recipe.getGramPerKcal())
                    .inStock(recipe.isInStock())
                    .imgUrl(imgUrl)
                    .build();
            recipeDtoList.add(recipeDto);
        }

        Subscribe subscribe = dog.getSubscribe();
        List<SubscribeRecipe> subscribeRecipes = subscribeRecipeRepository.findBySubscribe(subscribe);
        String recipeName = subscribeRecipes.get(0).getRecipe().getName();
        if (subscribeRecipes.size() > 1) {
            recipeName += "," + subscribeRecipes.get(1).getRecipe().getName();
        }

        SurveyResultResponseDto responseDto = SurveyResultResponseDto.builder()
                .dogId(dog.getId())
                .dogName(dog.getName())
                .subscribeId(subscribe.getId())
                .subscribeStatus(subscribe.getStatus())
                .recommendRecipeId(recommendRecipe.getId())
                .recommendRecipeName(recommendRecipe.getName())
                .recommendRecipeDescription(recommendRecipe.getDescription())
                .recommendRecipeImgUrl(url)
                .uiNameKorean(recommendRecipe.getUiNameKorean())
                .uiNameEnglish(recommendRecipe.getUiNameEnglish())
                .foodAnalysis(surveyReport.getFoodAnalysis())
                .recipeDtoList(recipeDtoList)
                .build();

        return responseDto;
    }
}
