package com.bi.barfdog.api;

import com.bi.barfdog.api.basketDto.QueryBasketsPageDto;
import com.bi.barfdog.api.basketDto.SaveBasketDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.basket.BasketRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/baskets",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class BasketApiController {

    private final ItemRepository itemRepository;
    private final BasketService basketService;
    private final BasketRepository basketRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @PostMapping
    public ResponseEntity createBasket(@CurrentUser Member member,
                                       @RequestBody @Valid SaveBasketDto requestDto,
                                       Errors errors) {
        Optional<Item> optionalItem = itemRepository.findById(requestDto.getItemId());
        if(!optionalItem.isPresent()) return notFound();

        basketService.createBasket(member, requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(BasketApiController.class).withSelfRel());
        representationModel.add(linkTo(BasketApiController.class).withRel("query_baskets"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-basket").withRel("profile"));

        return ResponseEntity.created(linkTo(BasketApiController.class).toUri()).body(representationModel);
    }

    @GetMapping
    public ResponseEntity getBaskets(@CurrentUser Member member) {

        QueryBasketsPageDto queryBasketsPageDto = basketService.getQueryBasketsPage(member);

        EntityModel<QueryBasketsPageDto> entityModel = EntityModel.of(queryBasketsPageDto,
                linkTo(BasketApiController.class).withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-query-baskets").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }











    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
