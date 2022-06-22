package com.bi.barfdog.api;

import com.bi.barfdog.api.basketDto.DeleteBasketsDto;
import com.bi.barfdog.api.basketDto.QueryBasketsPageDto;
import com.bi.barfdog.api.basketDto.SaveBasketDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.basket.Basket;
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
                linkTo(BasketApiController.class).withRel("delete_baskets"),
                profileRootUrlBuilder.slash("index.html#resources-query-baskets").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBasket(@PathVariable Long id) {
        Optional<Basket> optionalBasket = basketRepository.findById(id);
        if(!optionalBasket.isPresent()) return notFound();

        basketService.deleteBasket(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(BasketApiController.class).withSelfRel());
        representationModel.add(linkTo(BasketApiController.class).withRel("query_baskets"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-basket").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @DeleteMapping
    public ResponseEntity deleteBaskets(@CurrentUser Member member,
                                        @RequestBody @Valid DeleteBasketsDto requestDto,
                                        Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        basketService.deleteBaskets(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(BasketApiController.class).withSelfRel());
        representationModel.add(linkTo(BasketApiController.class).withRel("query_baskets"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-baskets").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PutMapping("/{id}/increase")
    public ResponseEntity increaseBasket(@PathVariable Long id) {
        Optional<Basket> optionalBasket = basketRepository.findById(id);
        if (!optionalBasket.isPresent()) return notFound();

        basketService.increaseBasket(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(BasketApiController.class).withSelfRel());
        representationModel.add(linkTo(BasketApiController.class).withRel("query_baskets"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-increase-basket").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @PutMapping("/{id}/decrease")
    public ResponseEntity decreaseBasket(@PathVariable Long id) {
        Optional<Basket> optionalBasket = basketRepository.findById(id);
        if (!optionalBasket.isPresent()) return notFound();

        basketService.decreaseBasket(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(BasketApiController.class).withSelfRel());
        representationModel.add(linkTo(BasketApiController.class).withRel("query_baskets"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-decrease-basket").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }











    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
