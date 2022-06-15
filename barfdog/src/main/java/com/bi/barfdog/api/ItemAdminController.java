package com.bi.barfdog.api;

import com.bi.barfdog.api.blogDto.UploadedImageAdminDto;
import com.bi.barfdog.api.itemDto.*;
import com.bi.barfdog.api.resource.ItemAdminDtoResource;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.service.ItemService;
import com.bi.barfdog.validator.ItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/items", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class ItemAdminController {

    private final ItemService itemService;

    private final ItemValidator itemValidator;

    private final ItemRepository itemRepository;

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

    @GetMapping("/{id}")
    public ResponseEntity queryItem(@PathVariable Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) return notFound();

        QueryItemAdminDto responseDto = itemService.queryItem(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ItemAdminController.class).slash(id);

        EntityModel<QueryItemAdminDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(ItemAdminController.class).withRel("query_items"),
                linkTo(ItemAdminController.class).slash("image").slash("upload").withRel("upload_itemImages"),
                linkTo(ItemAdminController.class).slash("contentImage").slash("upload").withRel("upload_itemContentImages"),
                linkTo(ItemAdminController.class).slash(id).withRel("update_item"),
                profileRootUrlBuilder.slash("index.html#resources-admin-query-item").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping
    public ResponseEntity queryItems(Pageable pageable,
                                     PagedResourcesAssembler<QueryItemsAdminDto> assembler,
                                     @ModelAttribute @Valid QueryItemsAdminRequestDto requestDto,
                                     BindingResult bindingResult) {
        if(bindingResult.hasErrors()) return badRequest(bindingResult);

        Page<QueryItemsAdminDto> page = itemRepository.findAdminDtoList(pageable, requestDto.getItemType());

        PagedModel<EntityModel<QueryItemsAdminDto>> entityModels = assembler.toModel(page, e -> new ItemAdminDtoResource(e));

        entityModels.add(linkTo(ItemAdminController.class).withRel("create_item"));
        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-query-items").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateItem(@PathVariable Long id,
                                     @RequestBody @Valid ItemUpdateDto requestDto,
                                     Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (!optionalItem.isPresent()) return notFound();

        itemService.updateItem(id, requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ItemAdminController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ItemAdminController.class).withRel("query_items"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-item").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteItem(@PathVariable Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if(!optionalItem.isPresent()) return notFound();

        itemService.deleteItem(id);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(ItemAdminController.class).slash(id);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(ItemAdminController.class).withRel("query_items"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-delete-item").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }






    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }



}
