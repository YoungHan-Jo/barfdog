package com.bi.barfdog.api;

import com.bi.barfdog.api.itemDto.*;
import com.bi.barfdog.api.resource.ItemReviewsResource;
import com.bi.barfdog.api.resource.ItemsDtoResource;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.review.ReviewRepository;
import com.bi.barfdog.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/items", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ItemApiController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @GetMapping
    public ResponseEntity queryItems(Pageable pageable,
                                     PagedResourcesAssembler<QueryItemsDto> assembler,
                                     @ModelAttribute @Valid ItemsCond cond,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return badRequest(bindingResult);

        Page<QueryItemsDto> page = itemRepository.findItemsDto(pageable, cond);

        PagedModel<ItemsDtoResource> pagedModel = assembler.toModel(page, e -> new ItemsDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-items").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity queryItem(@PathVariable Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) return notFound();

        QueryItemDto responseDto = itemRepository.findItemDtoById(id);

        EntityModel<QueryItemDto> entityModel = EntityModel.of(responseDto,
                linkTo(ItemApiController.class).slash(id).withSelfRel(),
                linkTo(ItemApiController.class).slash(id).slash("reviews").withRel("query_item_reviews"),
                profileRootUrlBuilder.slash("index.html#resources-query-item").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity queryItemReviews(@CurrentUser Member member,
                                           @PathVariable Long id,
                                           Pageable pageable,
                                           PagedResourcesAssembler<ItemReviewsDto> assembler) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) return notFound();

        Page<ItemReviewsDto> page = reviewRepository.findItemReviewsDtoByItemId(pageable, id);

        PagedModel<ItemReviewsResource> pagedModel = assembler.toModel(page, e -> new ItemReviewsResource(e));
        if (member.getRoleList().contains("ADMIN")) {
            pagedModel.add(linkTo(ReviewAdminController.class).slash("recipes").withRel("query_review_recipes"));
            pagedModel.add(linkTo(ReviewAdminController.class).slash("items").slash("ALL").withRel("query_review_items"));
            pagedModel.add(linkTo(ReviewAdminController.class).withRel("admin_create_review"));
        }
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-item-reviews").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }



    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }
}
