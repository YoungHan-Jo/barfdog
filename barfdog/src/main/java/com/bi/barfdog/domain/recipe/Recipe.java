package com.bi.barfdog.domain.recipe;

import com.bi.barfdog.api.recipeDto.RecipeRequestDto;
import com.bi.barfdog.domain.BaseTimeEntity;
import com.bi.barfdog.domain.subscribeRecipe.SubscribeRecipe;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity @Builder
public class Recipe extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "recipe_id")
    private Long id;

    private String name;

    private String description;

    private String uiNameKorean;
    private String uiNameEnglish;

    @Column(precision = 7,scale = 3)
    private BigDecimal pricePerGram;

    @Column(precision = 9,scale = 5)
    private BigDecimal gramPerKcal;

    private String ingredients; // 닭,오리,소 띄워쓰기 없이 콤마로 구분

    private String descriptionForSurvey;

    private ThumbnailImage thumbnailImage;

    @Enumerated(EnumType.STRING)
    private Leaked leaked; // 노출 여부

    private boolean inStock; // 재고 여부

    @Enumerated(EnumType.STRING)
    private RecipeStatus status;

    @OneToMany(mappedBy = "recipe")
    private List<SubscribeRecipe> subscribeRecipes = new ArrayList<>();

    public List<String> getIngredientList() {
        if (this.ingredients.length() > 0) {
            return Arrays.asList(this.ingredients.split(","));
        }
        return new ArrayList<>();
    }


    public void update(RecipeRequestDto requestDto, ThumbnailImage thumbnailImage) {
        if (thumbnailImage == null) {
            this.update(requestDto);
            return;
        }
        String folder = thumbnailImage.getFolder();
        String filename1 = thumbnailImage.getFilename1();
        String filename2 = thumbnailImage.getFilename2();

        if (filename1 == null) {
            this.thumbnailImage = new ThumbnailImage(folder, thumbnailImage.getFilename1(), filename2);
        } else if (filename2 == null) {
            this.thumbnailImage = new ThumbnailImage(folder, filename1, thumbnailImage.getFilename2());
        } else {
            this.thumbnailImage = new ThumbnailImage(folder, filename1, filename2);
        }

        this.update(requestDto);
    }

    private void update(RecipeRequestDto requestDto) {
        name = requestDto.getName();
        description = requestDto.getDescription();
        uiNameKorean = requestDto.getUiNameKorean();
        uiNameEnglish = requestDto.getUiNameEnglish();
        pricePerGram = new BigDecimal(requestDto.getPricePerGram());
        gramPerKcal = new BigDecimal(requestDto.getGramPerKcal());
        ingredients = requestDto.getIngredients();
        descriptionForSurvey = requestDto.getDescriptionForSurvey();
        leaked = requestDto.getLeaked();
        inStock = requestDto.isInStock();
    }

    public void inactive() {
        status = RecipeStatus.INACTIVE;
    }
}
