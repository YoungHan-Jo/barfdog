package com.bi.barfdog.api.dogDto;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.domain.member.Gender;
import com.bi.barfdog.domain.subscribe.SubscribeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryDogsDto {

    private Long id;
    private String pictureUrl;
    private String name;
    private String birth;
    private Gender gender;
    private boolean representative;
    private SubscribeStatus subscribeStatus;

    public void changeUrl(String filename) {
        pictureUrl = linkTo(InfoController.class).slash("display/dogs?filename=" + filename).toString();
    }
}
