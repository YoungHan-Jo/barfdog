package com.bi.barfdog.api;

import com.bi.barfdog.api.memberDto.QueryMembersCond;
import com.bi.barfdog.api.rewardDto.PublishToGroupDto;
import com.bi.barfdog.api.rewardDto.PublishToPersonalDto;
import com.bi.barfdog.api.rewardDto.QueryAdminRewardsDto;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.repository.reward.RewardRepository;
import com.bi.barfdog.service.RewardService;
import com.bi.barfdog.validator.MemberValidator;
import com.bi.barfdog.validator.RewardValidator;
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

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/rewards", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class RewardAdminController {

    private final RewardRepository rewardRepository;

    private final RewardService rewardService;

    private final RewardValidator rewardValidator;
    private final MemberValidator memberValidator;


    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @PostMapping("/personal")
    public ResponseEntity publishPersonal(@RequestBody @Valid PublishToPersonalDto requestDto,
                                          Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        memberValidator.validateMemberIdList(requestDto.getMemberIdList(), errors);
        if (errors.hasErrors()) return badRequest(errors);

        rewardService.publishToPersonal(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RewardAdminController.class).slash("personal");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(RewardAdminController.class).withRel("admin_query_rewards"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-publish-reward-personal").withRel("profile"));

        return ResponseEntity.created(linkTo(RewardAdminController.class).toUri()).body(representationModel);
    }

    @PostMapping("/group")
    public ResponseEntity publishGroup(@RequestBody @Valid PublishToGroupDto requestDto,
                                       Errors errors) {
        if(errors.hasErrors()) return badRequest(errors);
        rewardValidator.validateBirth(requestDto, errors);
        if(errors.hasErrors()) return badRequest(errors);

        rewardService.publishToGroup(requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RewardAdminController.class).slash("group");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(RewardAdminController.class).withRel("admin_query_rewards"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-publish-reward-group").withRel("profile"));

        return ResponseEntity.created(linkTo(RewardAdminController.class).toUri()).body(representationModel);
    }

    @GetMapping
    public ResponseEntity queryRewards(Pageable pageable,
                                       PagedResourcesAssembler<QueryAdminRewardsDto> assembler,
                                       @ModelAttribute @Valid QueryMembersCond cond,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return badRequest(bindingResult);
        memberValidator.wrongTerm(cond.getFrom(), cond.getTo(), bindingResult);
        if (bindingResult.hasErrors()) return badRequest(bindingResult);

        Page<QueryAdminRewardsDto> page = rewardRepository.findAdminRewardsDtoByCond(pageable, cond);

        PagedModel<EntityModel<QueryAdminRewardsDto>> entityModels = assembler.toModel(page);

        entityModels.add(linkTo(RewardAdminController.class).slash("personal").withRel("publish_reward_personal"));
        entityModels.add(linkTo(MemberApiController.class).slash("publication").withRel("query_member"));
        entityModels.add(linkTo(RewardAdminController.class).slash("personal").withRel("publish_reward_group"));
        entityModels.add(profileRootUrlBuilder.slash("index.html#resources-admin-queryRewards").withRel("profile"));

        return ResponseEntity.ok(entityModels);
    }





    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

}
