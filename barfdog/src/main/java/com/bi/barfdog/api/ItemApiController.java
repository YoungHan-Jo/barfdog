package com.bi.barfdog.api;

import com.bi.barfdog.api.itemDto.ItemSaveDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/items", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ItemApiController {

    private final ItemService itemService;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping
    public ResponseEntity createProducts(@RequestPart @Valid ItemSaveDto requestDto, Errors errors,
                                         @RequestPart(required = false) List<MultipartFile> imgFiles,
                                         @RequestPart(required = false) List<MultipartFile> contentImgFiles) {
        if(errors.hasErrors()) return badRequest(errors);

        itemService.createItem(requestDto, imgFiles, contentImgFiles);




        return ResponseEntity.created(null).body(null);
    }




    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }
}
