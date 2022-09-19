package com.bi.barfdog.service;

import com.bi.barfdog.api.BasketApiController;
import com.bi.barfdog.api.basketDto.DeleteBasketsDto;
import com.bi.barfdog.api.basketDto.QueryBasketsDto;
import com.bi.barfdog.api.basketDto.QueryBasketsPageDto;
import com.bi.barfdog.api.basketDto.SaveBasketDto;
import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.basket.BasketOption;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemOption;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.domain.setting.DeliveryConstant;
import com.bi.barfdog.repository.setting.SettingRepository;
import com.bi.barfdog.repository.basket.BasketRepository;
import com.bi.barfdog.repository.basket.BasketOptionRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bi.barfdog.api.basketDto.SaveBasketDto.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BasketService {

    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final BasketRepository basketRepository;
    private final BasketOptionRepository basketOptionRepository;
    private final SettingRepository settingRepository;

    @Transactional
    public void createBasket(Member member, SaveBasketDto requestDto) {

        Basket basket = null;

        Optional<Basket> optionalBasket = basketRepository.findByMemberAndItemId(member, requestDto.getItemId());
        if (optionalBasket.isPresent()) {
            basket = optionalBasket.get();
            basket.merge(requestDto.getItemAmount());
        } else {
            basket = saveBasket(member, requestDto);
        }

        saveOptionBaskets(requestDto, basket);
    }

    private Basket saveBasket(Member member, SaveBasketDto requestDto) {
        Item item = itemRepository.findById(requestDto.getItemId()).get();

        Basket basket = Basket.builder()
                .item(item)
                .member(member)
                .amount(requestDto.getItemAmount())
                .build();
        return basketRepository.save(basket);
    }

    private void saveOptionBaskets(SaveBasketDto requestDto, Basket basket) {
        List<OptionDto> optionDtoList = requestDto.getOptionDtoList();
        for (OptionDto optionDto : optionDtoList) {

            Long optionId = optionDto.getOptionId();

            Optional<BasketOption> optionalBasketOption = basketOptionRepository.findByOptionIdAndBasket(optionId, basket);

            if (optionalBasketOption.isPresent()) {
                BasketOption basketOption = optionalBasketOption.get();
                basketOption.merge(optionDto.getOptionAmount());
            } else {
                saveBasketOption(basket, optionDto);
            }

        }
    }

    private void saveBasketOption(Basket basket, OptionDto optionDto) {
        ItemOption itemOption = itemOptionRepository.findById(optionDto.getOptionId()).get();

        BasketOption basketOption = BasketOption.builder()
                .basket(basket)
                .itemOption(itemOption)
                .amount(optionDto.getOptionAmount())
                .build();
        basketOptionRepository.save(basketOption);
    }

    public QueryBasketsPageDto getQueryBasketsPage(Member member) {
        List<EntityModel<QueryBasketsDto>> basketDtoList = new ArrayList<>();

        List<QueryBasketsDto> responseDto = basketRepository.findBasketsDto(member);

        for (QueryBasketsDto dto : responseDto) {
            EntityModel<QueryBasketsDto> entityModel = EntityModel.of(dto,
                    linkTo(BasketApiController.class).slash(dto.getItemDto().getBasketId()).slash("increase").withRel("increase_basket"),
                    linkTo(BasketApiController.class).slash(dto.getItemDto().getBasketId()).slash("decrease").withRel("decrease_basket"),
                    linkTo(BasketApiController.class).slash(dto.getItemDto().getBasketId()).withRel("delete_basket")
            );
            basketDtoList.add(entityModel);
        }


        DeliveryConstant deliveryConstant = settingRepository.findDeliveryConstant();

        return QueryBasketsPageDto.builder()
                .deliveryConstant(deliveryConstant)
                .basketDtoList(basketDtoList)
                .build();
    }

    @Transactional
    public void deleteBasket(Long id) {
        Basket basket = basketRepository.findById(id).get();
        List<BasketOption> basketOptions = basketOptionRepository.findAllByBasket(basket);
        basketOptionRepository.deleteAll(basketOptions);
        basketRepository.delete(basket);
    }

    @Transactional
    public void deleteBaskets(DeleteBasketsDto requestDto) {
        List<Long> deleteBasketIdList = requestDto.getDeleteBasketIdList();
        basketOptionRepository.deleteAllByBasketIdList(deleteBasketIdList);
        basketRepository.deleteAllById(deleteBasketIdList);
    }

    @Transactional
    public void increaseBasket(Long id) {
        Basket basket = basketRepository.findById(id).get();
        basket.increase();
    }

    @Transactional
    public void decreaseBasket(Long id) {
        Basket basket = basketRepository.findById(id).get();
        basket.decrease();
    }
}
