package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.UploadedImageAdminDto;
import com.bi.barfdog.api.itemDto.ItemSaveDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.service.ItemService;
import com.bi.barfdog.validator.ItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/items", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ItemAdminController {

    private final ItemService itemService;

    private final ItemValidator itemValidator;

    private WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @PostMapping("/image/upload")
    public ResponseEntity uploadItemImage(@RequestPart MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().build();

        UploadedImageAdminDto responseDto = itemService.uploadItemImageFile(file);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ItemAdminController.class).slash("image").slash("upload");

        EntityModel<UploadedImageAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-upload-itemImage").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/contentImage/upload")
    public ResponseEntity uploadItemContentImage(@RequestPart MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().build();

        UploadedImageAdminDto responseDto = itemService.uploadItemContentImageFile(file);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ItemAdminController.class).slash("contentImage").slash("upload");

        EntityModel<UploadedImageAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                profileRootUrlBuilder.slash("index.html#resources-upload-itemContentImage").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }



    @PostMapping
    public ResponseEntity createProducts(@RequestBody @Valid ItemSaveDto requestDto,
                                         Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        itemValidator.validateImage(requestDto, errors);
        if(errors.hasErrors()) return badRequest(errors);

        itemService.createItem(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ItemAdminController.class);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ItemAdminController.class).withRel("admin_query_items"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-item").withRel("profile"));

        return ResponseEntity.created(linkTo(ItemAdminController.class).toUri()).body(representationModel);
    }






    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }



}
