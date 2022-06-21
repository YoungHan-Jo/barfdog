package com.bi.barfdog.api;

import com.bi.barfdog.api.basketDto.SaveBasketDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.basket.BasketOption;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.Item;
import com.bi.barfdog.domain.item.ItemOption;
import com.bi.barfdog.domain.item.ItemStatus;
import com.bi.barfdog.domain.item.ItemType;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.basket.BasketRepository;
import com.bi.barfdog.repository.basket.BasketOptionRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.member.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class BasketApiControllerTest extends BaseTest {

    @Autowired
    EntityManager em;
    @Autowired
    AppProperties appProperties;
    @Autowired
    ItemOptionRepository itemOptionRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    BasketRepository basketRepository;
    @Autowired
    BasketOptionRepository basketOptionRepository;
    @Autowired
    MemberRepository memberRepository;


    @Test
    @DisplayName("정상적으로 장바구니에 담는 테스트")
    public void createBasket() throws Exception {
       //given

        Item item = generateItem(1);
        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);
        ItemOption option3 = generateOption(item, 3);

        List<SaveBasketDto.OptionDto> optionDtoList = new ArrayList<>();

        SaveBasketDto.OptionDto optionDto1 = new SaveBasketDto.OptionDto(option1.getId(), 1);
        optionDtoList.add(optionDto1);
        SaveBasketDto.OptionDto optionDto3 = new SaveBasketDto.OptionDto(option3.getId(), 3);
        optionDtoList.add(optionDto3);

        int itemAmount = 5;
        SaveBasketDto requestDto = SaveBasketDto.builder()
                .itemId(item.getId())
                .itemAmount(itemAmount)
                .optionDtoList(optionDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/baskets")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create_basket",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_baskets").description("장바구니 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("itemId").description("상품 id"),
                                fieldWithPath("itemAmount").description("상품 개수"),
                                fieldWithPath("optionDtoList[0].optionId").description("상품 옵션 id"),
                                fieldWithPath("optionDtoList[0].optionAmount").description("상품 옵션 개수")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_baskets.href").description("장바구니 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
        ;

        em.flush();
        em.clear();

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        List<Basket> baskets = basketRepository.findAll();
        assertThat(baskets.size()).isEqualTo(1);
        Basket basket = baskets.get(0);
        assertThat(basket.getItem().getId()).isEqualTo(item.getId());
        assertThat(basket.getAmount()).isEqualTo(itemAmount);
        assertThat(basket.getMember().getId()).isEqualTo(member.getId());

        List<BasketOption> basketOptions = basketOptionRepository.findAllByBasket(basket);
        assertThat(basketOptions.size()).isEqualTo(2);
        assertThat(basketOptions.get(0).getAmount()).isEqualTo(1);
        assertThat(basketOptions.get(0).getBasket().getId()).isEqualTo(basket.getId());
        assertThat(basketOptions.get(1).getAmount()).isEqualTo(3);

    }

    @Test
    @DisplayName("정상적으로 장바구니 조회")
    public void queryBaskets() throws Exception {
       //given

       //when & then
        mockMvc.perform(get("/api/baskets")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;


    }





    private Item generateItem(int i) {
        Item item = Item.builder()
                .itemType(ItemType.RAW)
                .name("상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .itemIcons("BEST,NEW")
                .deliveryFree(true)
                .status(ItemStatus.LEAKED)
                .build();
        return itemRepository.save(item);
    }

    private ItemOption generateOption(Item item, int i) {
        ItemOption itemOption = ItemOption.builder()
                .item(item)
                .name("옵션" + i)
                .optionPrice(i * 1000)
                .remaining(999)
                .build();
        return itemOptionRepository.save(itemOption);
    }



    private String getAdminToken() throws Exception {
        return getBearerToken(appProperties.getAdminEmail(), appProperties.getAdminPassword());
    }

    private String getUserToken() throws Exception {
        return getBearerToken(appProperties.getUserEmail(), appProperties.getUserPassword());
    }

    private String getBearerToken(String appProperties, String appProperties1) throws Exception {
        JwtLoginDto requestDto = JwtLoginDto.builder()
                .email(appProperties)
                .password(appProperties1)
                .build();

        //when & then
        ResultActions perform = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        MockHttpServletResponse response = perform.andReturn().getResponse();
        return response.getHeaders("Authorization").get(0);
    }


}