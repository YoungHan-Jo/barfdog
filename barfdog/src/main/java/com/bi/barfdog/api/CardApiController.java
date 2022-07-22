package com.bi.barfdog.api;

import com.bi.barfdog.api.cardDto.ChangeCardDto;
import com.bi.barfdog.api.cardDto.QuerySubscribeCardsDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.subscribe.Subscribe;
import com.bi.barfdog.repository.card.CardRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/cards",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class CardApiController {

    private final CardRepository cardRepository;
    private final CardService cardService;
    private final SubscribeRepository subscribeRepository;

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

    @PostMapping("/subscribes/{id}")
    public ResponseEntity changeCard(@CurrentUser Member member,
                                     @PathVariable Long id,
                                     @RequestBody @Valid ChangeCardDto requestDto,
                                     Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findById(id);
        if (!optionalSubscribe.isPresent()) return notFound();

        cardService.changeCard(member, id, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(CardApiController.class).slash("subscribes").slash(id).withSelfRel());
        representationModel.add(linkTo(CardApiController.class).withRel("query_cards"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-change-card").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }















    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
