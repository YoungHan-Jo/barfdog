package com.bi.barfdog.api;

import com.bi.barfdog.api.cardDto.QuerySubscribeCardsDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.card.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/cards",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class CardApiController {

    private final CardRepository cardRepository;


    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping
    public ResponseEntity querySubscribeCards(@CurrentUser Member member) {

        List<QuerySubscribeCardsDto> responseDtoList = cardRepository.findSubscribeCards(member);

        List<EntityModel<QuerySubscribeCardsDto>> entityModelList = new ArrayList<>();

        for (QuerySubscribeCardsDto querySubscribeCardsDto : responseDtoList) {
            EntityModel<QuerySubscribeCardsDto> entityModel = EntityModel.of(querySubscribeCardsDto,
                    linkTo(CardApiController.class).slash("subscribe").slash(querySubscribeCardsDto.getSubscribeCardDto().getSubscribeId()).withRel("change_card")
            );
            entityModelList.add(entityModel);
        }

        CollectionModel<EntityModel<QuerySubscribeCardsDto>> collectionModel = CollectionModel.of(entityModelList,
                linkTo(CardApiController.class).withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-subscribeCards").withRel("profile")
        );

        return ResponseEntity.ok(collectionModel);
    }















    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
