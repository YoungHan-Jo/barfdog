package com.bi.barfdog.api;

import com.bi.barfdog.api.deliveryDto.QueryGeneralDeliveriesDto;
import com.bi.barfdog.api.deliveryDto.QuerySubscribeDeliveriesDto;
import com.bi.barfdog.api.deliveryDto.UpdateDeliveryNumberDto;
import com.bi.barfdog.api.resource.GeneralDeliveriesDtoResource;
import com.bi.barfdog.api.resource.SubscribeDeliveriesDtoResource;
import com.bi.barfdog.auth.CurrentUser;
import com.bi.barfdog.common.ErrorsResource;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.goodsFlow.GoodsFlowRequestDto;
import com.bi.barfdog.goodsFlow.GoodsFlowResponseDto;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@RequestMapping(value = "/api/deliveries", produces = MediaTypes.HAL_JSON_VALUE)
@RestController
public class DeliveryApiController {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;
    private GoodsFlowResponseDto responseDto;
    private UpdateDeliveryNumberDto updateDeliveryNumberDto;
    private UpdateDeliveryNumberDto.DeliveryNumberDto deliveryNumberDto;
    private List<UpdateDeliveryNumberDto.DeliveryNumberDto> list;

    WebMvcLinkBuilder profileRootUrlBuilder = linkTo(IndexApiController.class).slash("docs");


    @GetMapping("/subscribe")
    public ResponseEntity querySubscribeDeliveries(@CurrentUser Member member,
                                                   Pageable pageable,
                                                   PagedResourcesAssembler<QuerySubscribeDeliveriesDto> assembler) {

        Page<QuerySubscribeDeliveriesDto> page = deliveryRepository.findSubscribeDeliveriesDto(member, pageable);

        PagedModel<SubscribeDeliveriesDtoResource> pagedModel = assembler.toModel(page, e -> new SubscribeDeliveriesDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-deliveries-subscribe").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/general")
    public ResponseEntity queryGeneralDeliveries(@CurrentUser Member member,
                                                 Pageable pageable,
                                                 PagedResourcesAssembler<QueryGeneralDeliveriesDto> assembler) {

        Page<QueryGeneralDeliveriesDto> page = deliveryRepository.findGeneralDeliveriesDto(member, pageable);

        PagedModel<GeneralDeliveriesDtoResource> pagedModel = assembler.toModel(page, e -> new GeneralDeliveriesDtoResource(e));
        pagedModel.add(profileRootUrlBuilder.slash("index.html#resources-query-deliveries-general").withRel("profile"));

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping(value = "/goodsFlow/postTraceResult", produces = MediaType.APPLICATION_JSON_VALUE)
    public GoodsFlowResponseDto GoodsFlowData(
                                        @RequestBody @Valid Optional<GoodsFlowRequestDto> requestDto,
                                        Errors errors) {
        list = new ArrayList<>();

        if(errors.hasErrors()) {
            responseDto = GoodsFlowResponseDto.fail();
        } else {
            // 운송장 등록
            try {
                for (GoodsFlowRequestDto.RequestDto item : requestDto.get().getItems()) {
                    deliveryNumberDto = UpdateDeliveryNumberDto.DeliveryNumberDto.builder()
                            .transUniqueCd(item.getTransUniqueCd())
                            .deliveryNumber(item.getSheetNo())
                            .build();
                    list.add(deliveryNumberDto);
                }

                updateDeliveryNumberDto = UpdateDeliveryNumberDto.builder()
                        .deliveryNumberDtoList(list)
                        .build();

                deliveryService.setDeliveryNumber(updateDeliveryNumberDto);
                responseDto = GoodsFlowResponseDto.success();
            } catch (Exception e) {
                e.printStackTrace();
                responseDto = GoodsFlowResponseDto.fail();
            }
        }


        EntityModel<GoodsFlowResponseDto> entityModel = EntityModel.of(responseDto);

        return responseDto;
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

    private ResponseEntity<Object> notFound() {
        return ResponseEntity.notFound().build();
    }

    private ResponseEntity<EntityModel<Errors>> conflict(Errors errors) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorsResource(errors));
    }


}
