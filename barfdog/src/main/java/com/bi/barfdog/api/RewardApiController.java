package com.bi.barfdog.api;

import com.bi.barfdog.api.resource.RewardDtoResource;
import com.bi.barfdog.api.rewardDto.QueryInvitePageDto;
import com.bi.barfdog.api.rewardDto.QueryRewardsDto;
import com.bi.barfdog.api.rewardDto.QueryRewardsPageDto;
import com.bi.barfdog.api.rewardDto.RecommendFriendDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.member.MemberRepository;
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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/rewards", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class RewardApiController {

    private final RewardRepository rewardRepository;
    private final MemberRepository memberRepository;
    private final RewardService rewardService;

    private final RewardValidator rewardValidator;
    private final MemberValidator memberValidator;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping
    public ResponseEntity queryRewards(@CurrentUser Member member,
                                       Pageable pageable,
                                       PagedResourcesAssembler<QueryRewardsDto> assembler) {

        Page<QueryRewardsDto> page = rewardRepository.findRewardsDto(member, pageable);
        PagedModel<RewardDtoResource> pagedModel = assembler.toModel(page, e -> new RewardDtoResource(e));

        EntityModel<QueryRewardsPageDto> entityModel = getRewardPageEntityModel(member, pagedModel);
        entityModel.add(profileRootUrlBuilder.slash("index.html#resources-query-rewards").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/invite")
    public ResponseEntity queryRewardsInvite(@CurrentUser Member member,
                                             Pageable pageable,
                                             PagedResourcesAssembler<QueryRewardsDto> assembler) {

        Page<QueryRewardsDto> page = rewardRepository.findRewardsDtoInvite(member, pageable);
        PagedModel<RewardDtoResource> pagedModel = assembler.toModel(page, e -> new RewardDtoResource(e));

        EntityModel<QueryInvitePageDto> entityModel = getInvitePageEntityModel(member, pagedModel);
        entityModel.add(linkTo(RewardApiController.class).slash("recommend").withRel("recommend_friend"));
        entityModel.add(profileRootUrlBuilder.slash("index.html#resources-query-rewards-invite").withRel("profile"));

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/recommend")
    public ResponseEntity recommendFriend(@CurrentUser Member member,
                                          @RequestBody @Valid RecommendFriendDto requestDto,
                                          Errors errors) {
        if (errors.hasErrors()) return badRequest(errors);
        Optional<Member> optionalMember = memberRepository.findByMyRecommendationCode(requestDto.getRecommendCode());
        if(!optionalMember.isPresent()) return notFound();
        memberValidator.validateHadRecommended(member, requestDto.getRecommendCode(), errors);
        if (errors.hasErrors()) return badRequest(errors);

        rewardService.recommendFriend(member.getId(), requestDto);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(RewardApiController.class).slash("recommend");

        RepresentationModel representationModel = new RepresentationModel();
        representationModel.add(selfLinkBuilder.withSelfRel());
        representationModel.add(linkTo(RewardApiController.class).slash("invite").withRel("query_rewards_invite"));
        representationModel.add(profileRootUrlBuilder.slash("index.html#resources-recommend-friend").withRel("profile"));

        return ResponseEntity.ok(representationModel);
    }





    private EntityModel<QueryInvitePageDto> getInvitePageEntityModel(Member member, PagedModel<RewardDtoResource> pagedModel) {
        Long joinedCount = memberRepository.findCountByMyCode(member.getMyRecommendationCode());
        Long orderedCount = rewardRepository.findInviteCount(member);
        int totalRewards = rewardRepository.findTotalRewardInvite(member);

        QueryInvitePageDto responseDto = QueryInvitePageDto.builder()
                .recommend(member.getRecommendCode())
                .joinedCount(joinedCount)
                .orderedCount(orderedCount)
                .totalRewards(totalRewards)
                .pagedModel(pagedModel)
                .build();

        EntityModel<QueryInvitePageDto> entityModel = EntityModel.of(responseDto);
        return entityModel;
    }


    private EntityModel<QueryRewardsPageDto> getRewardPageEntityModel(Member member, PagedModel<RewardDtoResource> pagedModel) {
        int reward = memberRepository.findRewardById(member.getId());

        QueryRewardsPageDto responseDto = QueryRewardsPageDto.builder()
                .reward(reward)
                .pagedModel(pagedModel)
                .build();
        EntityModel<QueryRewardsPageDto> entityModel = EntityModel.of(responseDto);
        return entityModel;
    }

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }



}
