package com.bi.barfdog.api.barfDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.reviewDto.QueryBestReviewsDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomePageDto {

    private TopBannerDto topBannerDto;
    @Builder.Default
    private List<MainBannerDto> mainBannerDtoList = new ArrayList<>();
    @Builder.Default
    private List<RecipeDto> recipeDtoList = new ArrayList<>();
    private List<QueryBestReviewsDto> queryBestReviewsDtoList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class TopBannerDto {
        private String name;
        private String backgroundColor;
        private String fontColor;
        private String pcLinkUrl;
        private String mobileLinkUrl;
    }

    @Data
    @AllArgsConstructor
    public static class MainBannerDto {
        private Long id;
        private int leakedOrder;
        private String name;

        private String pcFilename;
        private String pcImageUrl;
        private String pcLinkUrl;

        private String mobileFilename;
        private String mobileImageUrl;
        private String mobileLinkUrl;

        public void changeUrl() {
            this.pcImageUrl = linkTo(InfoController.class).slash("display/banners?filename=" + this.pcFilename).toString();
            this.mobileImageUrl = linkTo(InfoController.class).slash("display/banners?filename=" + this.mobileFilename).toString();
        }
    }

    @Data
    @AllArgsConstructor
    public static class RecipeDto {
        private Long id;

        private String name;

        private String description;

        private String uiNameKorean;
        private String uiNameEnglish;

        private String filename1;
        private String imageUrl1;

        private String filename2;
        private String imageUrl2;

        public void changeUrl() {
            this.imageUrl1 = linkTo(InfoController.class).slash("display/recipes?filename=" + filename1).toString();
            this.imageUrl2 = linkTo(InfoController.class).slash("display/recipes?filename=" + filename2).toString();
        }
    }


}
