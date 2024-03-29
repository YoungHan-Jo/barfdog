package com.bi.barfdog.api;

import com.bi.barfdog.api.barfDto.FriendTalkAllDto;
import com.bi.barfdog.api.barfDto.FriendTalkGroupDto;
import com.bi.barfdog.api.guestDto.QueryAdminGuestDto;
import com.bi.barfdog.api.guestDto.QueryGuestCond;
import com.bi.barfdog.api.guestDto.SaveGuestRequest;
import com.bi.barfdog.api.resource.AdminGuestDtoResource;
import com.bi.barfdog.api.resource.AdminOrdersDtoResource;
import com.bi.barfdog.api.settingDto.UpdateSettingDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.directsend.DirectSendResponseDto;
import com.bi.barfdog.directsend.FriendTalkResponseDto;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.repository.article.ArticleRepository;
import com.bi.barfdog.repository.blog.BlogRepository;
import com.bi.barfdog.repository.guest.GuestRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.service.*;
import com.bi.barfdog.validator.BlogValidator;
import com.bi.barfdog.validator.MemberValidator;
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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin",produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class AdminApiController {

    private final SettingRepository settingRepository;
    private final SettingService settingService;
    private final BarfService barfService;
    private final GuestService guestService;
    private final GuestRepository guestRepository;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping("/setting")
    public ResponseEntity querySetting() {

        Setting setting = settingRepository.findAll().get(0);

        EntityModel<Setting> entityModel = EntityModel.of(setting);
        entityModel.add(linkTo(AdminApiController.class).slash("setting").withSelfRel());
        entityModel.add(linkTo(AdminApiController.class).slash("setting").withRel("update_setting"));
        entityModel.add(profileRootUrlBuilder.slash("index.html#resources-query-setting").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/setting")
    public ResponseEntity updateSetting(@RequestBody @Valid UpdateSettingDto requestDto,
                                        Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        settingService.updateSetting(requestDto);

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(AdminApiController.class).slash("setting").withSelfRel());
        representationModel.add(linkTo(AdminApiController.class).slash("setting").withRel("query_setting"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-update-setting").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }



    @PostMapping("/friendTalk/all")
    public ResponseEntity friendTalkAll(@RequestBody @Valid FriendTalkAllDto requestDto,
                                        Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        FriendTalkResponseDto responseDto = barfService.sendFriendTalkAll(requestDto);

        EntityModel<FriendTalkResponseDto> entityModel = EntityModel.of(responseDto);
        entityModel.add(linkTo(AdminApiController.class).slash("friendTalk/all").withSelfRel());
        entityModel.add(profileRootUrlBuilder.slash("index.html#resources-friendTalk-all").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PostMapping("/friendTalk/group")
    public ResponseEntity friendTalkGroup(@RequestBody @Valid FriendTalkGroupDto requestDto,
                                          Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        FriendTalkResponseDto responseDto = barfService.sendFriendTalkGroup(requestDto);

        EntityModel<FriendTalkResponseDto> entityModel = EntityModel.of(responseDto);
        entityModel.add(linkTo(AdminApiController.class).slash("friendTalk/group").withSelfRel());
        entityModel.add(profileRootUrlBuilder.slash("index.html#resources-friendTalk-group").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }


    @PostMapping("/guests")
    public ResponseEntity createGuests(@RequestBody @Valid SaveGuestRequest requestDto,
                                       Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);

        guestService.createGuest(requestDto.getName(), requestDto.getPhoneNumber(), requestDto.getEmail());

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(linkTo(AdminApiController.class).slash("guests").withSelfRel());
        representationModel.add(linkTo(AdminApiController.class).slash("guests").withRel("query_guests"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-create-guest").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }

    @GetMapping("/guests")
    public ResponseEntity queryGuests(Pageable pageable,
                                      PagedResourcesAssembler<QueryAdminGuestDto> assembler,
                                      @ModelAttribute QueryGuestCond cond) {

        Page<QueryAdminGuestDto> page = guestRepository.findAdminGuestDtos(pageable, cond);

        PagedModel<AdminGuestDtoResource> pagedModel = assembler.toModel(page, e -> new AdminGuestDtoResource(e));

        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-admin-guests").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

















    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
