package com.bi.barfdog.api;

import com.bi.barfdog.api.settingDto.UpdateSettingDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.setting.Setting;
import com.bi.barfdog.repository.article.ArticleRepository;
import com.bi.barfdog.repository.blog.BlogRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.subscribe.SubscribeRepository;
import com.bi.barfdog.service.BlogService;
import com.bi.barfdog.service.MemberService;
import com.bi.barfdog.service.SettingService;
import com.bi.barfdog.validator.BlogValidator;
import com.bi.barfdog.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
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



















    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
