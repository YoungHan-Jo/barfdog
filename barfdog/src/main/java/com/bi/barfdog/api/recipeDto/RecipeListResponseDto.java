package com.bi.barfdog.api.recipeDto;

import com.bi.barfdog.domain.recipe.Leaked;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeListResponseDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal pricePerGram;
    private BigDecimal gramPerKcal;
    private String ingredients;
    private Leaked leaked;
    private boolean inStock;
    private LocalDate modifiedDate;

}
