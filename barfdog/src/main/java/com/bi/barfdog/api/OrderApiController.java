package com.bi.barfdog.api;

import com.bi.barfdog.api.orderDto.OrderSheetSubscribeResponseDto;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import com.bi.barfdog.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/orders", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class OrderApiController {

    private final MemberRepository memberRepository;
    private final OrderService orderService;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");

    @GetMapping("/sheet/subscribe")
    public ResponseEntity queryOrderSheetSubscribe(@CurrentUser Member member) {

        OrderSheetSubscribeResponseDto responseDto = orderService.getOrderSheetSubsDto(member);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(OrderApiController.class).slash("sheet").slash("subscribe");

        EntityModel<OrderSheetSubscribeResponseDto> entityModel = EntityModel.of(responseDto,
                selfLinkBuilder.withSelfRel(),
                linkTo(OrderApiController.class).slash("subscribe").withRel("order_subscribe"),
                profileRootUrlBuilder.slash("index.html#resources-create-recipe").withRel("profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/sheet/general")
    public void queryOrderSheetGeneral() {

    }

}
