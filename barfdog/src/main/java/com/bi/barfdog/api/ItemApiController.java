package com.bi.barfdog.api;

import com.bi.barfdog.api.itemDto.ItemSaveDto;
import com.bi.barfdog.api.itemDto.ItemsCond;
import com.bi.barfdog.api.itemDto.QueryItemsDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/items", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ItemApiController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @GetMapping
    public ResponseEntity queryItems(Pageable pageable,
                                     PagedResourcesAssembler<QueryItemsDto> assembler,
                                     @ModelAttribute @Valid ItemsCond cond,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return badRequest(bindingResult);

        Page<QueryItemsDto> page = itemRepository.findItemsDto(pageable, cond);


        return ResponseEntity.ok(page);
    }



    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }
}
