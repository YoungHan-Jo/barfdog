package com.bi.barfdog.api.reviewDto;

import com.bi.barfdog.api.InfoController;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueryReviewImagesDto {

    private String filename;
    private String url;

    public void changeUrl(String filename) {
        this.url = linkTo(InfoController.class).slash("display/reviews?filename=" + filename).toString();
    }
}
