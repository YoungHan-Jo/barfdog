package com.bi.barfdog.api.barfDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.member.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@Builder
public class MypageDto {

    private MypageMemberDto mypageMemberDto;
    private MypageDogDto mypageDogDto;

    private Long deliveryCount;
    private Long couponCount;

    @Data
    @AllArgsConstructor
    public static class MypageMemberDto {

        private Long id;
        private String memberName;
        private Grade grade;
        private String myRecommendationCode;
        private int reward;
    }

    @Data
    @AllArgsConstructor
    public static class MypageDogDto {
        private String thumbnailUrl;
        private String dogName;

        public void changeUrl(String filename) {
            if (filename != null) {
                thumbnailUrl = linkTo(InfoController.class).slash("display/dogs?filename=" + filename).toString();
            }
        }
    }


}
