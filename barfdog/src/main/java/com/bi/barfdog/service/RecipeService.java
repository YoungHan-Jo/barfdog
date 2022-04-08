package com.bi.barfdog.service;

import com.bi.barfdog.api.recipeDto.RecipeRequestDto;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.recipe.Recipe;
import com.bi.barfdog.domain.recipe.ThumbnailImage;
import com.bi.barfdog.repository.RecipeRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    private final StorageService storageService;

    @Transactional
    public void save(RecipeRequestDto requestDto, MultipartFile file1, MultipartFile file2) {

        ImgFilenamePath store1 = storageService.storeRecipeImg(file1);
        ImgFilenamePath store2 = storageService.storeRecipeImg(file2);

        String folder = store1.getFolder();
        String filename1 = store1.getFilename();
        String filename2 = store2.getFilename();

        Recipe recipe = Recipe.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .uiNameKorean(requestDto.getUiNameKorean())
                .uiNameEnglish(requestDto.getUiNameEnglish())
                .pricePerGram(new BigDecimal(requestDto.getPricePerGram()))
                .gramPerKcal(new BigDecimal(requestDto.getGramPerKcal()))
                .ingredients(requestDto.getIngredients())
                .descriptionForSurvey(requestDto.getDescriptionForSurvey())
                .thumbnailImage(new ThumbnailImage(folder, filename1, filename2))
                .leaked(requestDto.getLeaked())
                .inStock(requestDto.isInStock())
                .build();

        Recipe saveRecipe = recipeRepository.save(recipe);

        System.out.println("saveRecipe = " + saveRecipe);

    }

    @Transactional
    public void updateRecipe(Long id, RecipeRequestDto requestDto, MultipartFile file1, MultipartFile file2) {

        ThumbnailImage thumbnailImage = saveFilesAndGetInfo(file1, file2);

        Recipe recipe = recipeRepository.findById(id).get();


        recipe.update(requestDto, thumbnailImage);




    }

    private ThumbnailImage saveFilesAndGetInfo(MultipartFile file1, MultipartFile file2) {
        if (file1 != null && file2 != null) {
            ImgFilenamePath store1 = storageService.storeRecipeImg(file1);
            ImgFilenamePath store2 = storageService.storeRecipeImg(file2);
            return new ThumbnailImage(store1.getFolder(), store1.getFilename(), store2.getFilename());
        } else if (file1 != null) {
            ImgFilenamePath store1 = storageService.storeRecipeImg(file1);
            return new ThumbnailImage(store1.getFolder(), store1.getFilename(), null);
        } else if (file2 != null) {
            ImgFilenamePath store2 = storageService.storeRecipeImg(file2);
            return new ThumbnailImage(store2.getFolder(), null, store2.getFilename());
        }

        return null;
    }
}
