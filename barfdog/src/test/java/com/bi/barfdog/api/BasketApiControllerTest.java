package com.bi.barfdog.api;

import com.bi.barfdog.api.basketDto.DeleteBasketsDto;
import com.bi.barfdog.api.basketDto.SaveBasketDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.basket.Basket;
import com.bi.barfdog.domain.basket.BasketOption;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.basket.BasketRepository;
import com.bi.barfdog.repository.basket.BasketOptionRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @Autowired
    ItemImageRepository itemImageRepository;


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
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        generateItemImage(item1, 1);
        generateItemImage(item1, 2);
        ItemOption option1 = generateOption(item1, 1);
        ItemOption option2 = generateOption(item1, 2);

        Item item2 = generateItem(2);
        generateItemImage(item2, 1);
        generateItemImage(item2, 2);
        ItemOption option3 = generateOption(item2, 3);
        ItemOption option4 = generateOption(item2, 4);

        Basket basket1 = generateBasket(member, item1, 1);
        generateBasketOption(option1, basket1, 1);
        generateBasketOption(option2, basket1, 2);

        Basket basket2 = generateBasket(member, item2, 2);
        generateBasketOption(option3, basket2, 3);
        generateBasketOption(option4, basket2, 4);

        Basket basket3 = generateBasket(admin, item2, 2);
        generateBasketOption(option3, basket3, 5);
        generateBasketOption(option4, basket3, 6);

        //when & then
        mockMvc.perform(get("/api/baskets")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("deliveryConstant").exists())
                .andExpect(jsonPath("basketDtoList", hasSize(2)))
                .andDo(document("query_baskets",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("delete_baskets").description("장바구니 목록 여러개 삭제 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("deliveryConstant.price").description("배송비"),
                                fieldWithPath("deliveryConstant.freeCondition").description("배송비 무료 조건 xx원 이상"),
                                fieldWithPath("basketDtoList[0].itemDto.basketId").description("장바구니 id"),
                                fieldWithPath("basketDtoList[0].itemDto.itemId").description("상품 id"),
                                fieldWithPath("basketDtoList[0].itemDto.thumbnailUrl").description("상품 대표이미지 url"),
                                fieldWithPath("basketDtoList[0].itemDto.name").description("상품 이름"),
                                fieldWithPath("basketDtoList[0].itemDto.originalPrice").description("상품 원가"),
                                fieldWithPath("basketDtoList[0].itemDto.salePrice").description("할인 적용 후 판매 가격"),
                                fieldWithPath("basketDtoList[0].itemDto.amount").description("상품 개수"),
                                fieldWithPath("basketDtoList[0].itemDto.deliveryFree").description("배송비 무료 여부 true/false"),
                                fieldWithPath("basketDtoList[0].itemOptionDtoList[0].id").description("상품 옵션 id"),
                                fieldWithPath("basketDtoList[0].itemOptionDtoList[0].name").description("상품 옵션 이름"),
                                fieldWithPath("basketDtoList[0].itemOptionDtoList[0].optionPrice").description("상품 옵션 가격"),
                                fieldWithPath("basketDtoList[0].itemOptionDtoList[0].amount").description("상품 옵션 개수"),
                                fieldWithPath("basketDtoList[0].totalPrice").description("장바구니 목록 하나 당 합계 가격"),
                                fieldWithPath("basketDtoList[0]._links.increase_basket.href").description("장바구니에 해당 상품 1개 추가 링크"),
                                fieldWithPath("basketDtoList[0]._links.decrease_basket.href").description("장바구니에 해당 상품 1개 감소 링크"),
                                fieldWithPath("basketDtoList[0]._links.delete_basket.href").description("해당 상품 장바구니에서 삭제"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.delete_baskets.href").description("장바구니 목록 여러개 삭제 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }
    
    
    @Test
    @DisplayName("정상적으로 장바구니에서 물품 한개 삭제")
    public void deleteBasket() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        generateItemImage(item, 1);
        generateItemImage(item, 2);
        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        Basket basket = generateBasket(member, item, 1);
        generateBasketOption(option1, basket, 1);
        generateBasketOption(option2, basket, 2);
       
       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/baskets/{id}", basket.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete_basket",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_baskets").description("장바구니 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("삭제할 장바구니 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
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


        em.flush();
        em.clear();

        Optional<Basket> optionalBasket = basketRepository.findById(basket.getId());
        assertThat(optionalBasket.isPresent()).isFalse();

        List<BasketOption> basketOptions = basketOptionRepository.findAll();
        assertThat(basketOptions.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("삭제할 장바구니가 존재하지않음")
    public void deleteBasket_404() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        generateItemImage(item, 1);
        generateItemImage(item, 2);
        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        Basket basket = generateBasket(member, item, 1);
        generateBasketOption(option1, basket, 1);
        generateBasketOption(option2, basket, 2);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/baskets/9999")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("정상적으로 장바구니 목록 여러개 삭제")
    public void deleteBaskets() throws Exception {
       //given

        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();
        Member admin = memberRepository.findByEmail(appProperties.getAdminEmail()).get();

        Item item1 = generateItem(1);
        generateItemImage(item1, 1);
        generateItemImage(item1, 2);
        ItemOption option1 = generateOption(item1, 1);
        ItemOption option2 = generateOption(item1, 2);

        Item item2 = generateItem(2);
        generateItemImage(item2, 1);
        generateItemImage(item2, 2);
        ItemOption option3 = generateOption(item2, 3);
        ItemOption option4 = generateOption(item2, 4);

        Item item3 = generateItem(2);
        generateItemImage(item3, 1);
        generateItemImage(item3, 2);
        ItemOption option5 = generateOption(item3, 5);
        ItemOption option6 = generateOption(item3, 6);

        Basket basket1 = generateBasket(member, item1, 1);
        generateBasketOption(option1, basket1, 1);
        generateBasketOption(option2, basket1, 2);

        Basket basket2 = generateBasket(member, item3, 2);
        generateBasketOption(option3, basket2, 3);
        generateBasketOption(option4, basket2, 4);

        Basket basket3 = generateBasket(member, item3, 2);
        generateBasketOption(option5, basket3, 5);
        generateBasketOption(option6, basket3, 6);

        List<Long> deleteBasketIdList = new ArrayList<>();
        deleteBasketIdList.add(basket1.getId());
        deleteBasketIdList.add(basket2.getId());

        DeleteBasketsDto requestDto = DeleteBasketsDto.builder()
                .deleteBasketIdList(deleteBasketIdList)
                .build();

        //when & then
        mockMvc.perform(delete("/api/baskets")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("delete_baskets",
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
                                fieldWithPath("deleteBasketIdList").description("삭제할 장바구니 id 리스트")
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

        em.flush();
        em.clear();

        Optional<Basket> optionalBasket1 = basketRepository.findById(basket1.getId());
        Optional<Basket> optionalBasket2 = basketRepository.findById(basket2.getId());
        Optional<Basket> optionalBasket3 = basketRepository.findById(basket3.getId());
        assertThat(optionalBasket1.isPresent()).isFalse();
        assertThat(optionalBasket2.isPresent()).isFalse();
        assertThat(optionalBasket3.isPresent()).isTrue();

        List<BasketOption> basketOptions1 = basketOptionRepository.findAllByBasket(basket1);
        List<BasketOption> basketOptions2 = basketOptionRepository.findAllByBasket(basket2);
        List<BasketOption> basketOptions3 = basketOptionRepository.findAllByBasket(basket3);
        assertThat(basketOptions1.size()).isEqualTo(0);
        assertThat(basketOptions2.size()).isEqualTo(0);
        assertThat(basketOptions3.size()).isEqualTo(2);

    }


    @Test
    @DisplayName("정상적으로 장바구니 물품 추가")
    public void increaseBasket() throws Exception {
       //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        generateItemImage(item, 1);
        generateItemImage(item, 2);
        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        int amount = 1;
        Basket basket = generateBasket(member, item, amount);
        generateBasketOption(option1, basket, 1);
        generateBasketOption(option2, basket, 2);

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/baskets/{id}/increase", basket.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("increase_basket",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_baskets").description("장바구니 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("개수 추가할 장바구니 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
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


        em.flush();
        em.clear();

        Basket findBasket = basketRepository.findById(basket.getId()).get();
        assertThat(findBasket.getAmount()).isEqualTo(amount + 1);

    }

    @Test
    @DisplayName("추가할 장바구니가 없음 404")
    public void increaseBasket_404() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        generateItemImage(item, 1);
        generateItemImage(item, 2);
        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        int amount = 1;
        Basket basket = generateBasket(member, item, amount);
        generateBasketOption(option1, basket, 1);
        generateBasketOption(option2, basket, 2);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/baskets/9999/increase")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("정상적으로 장바구니 물품 감소")
    public void decreaseBasket() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        generateItemImage(item, 1);
        generateItemImage(item, 2);
        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        int amount = 2;
        Basket basket = generateBasket(member, item, amount);
        generateBasketOption(option1, basket, 1);
        generateBasketOption(option2, basket, 2);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/baskets/{id}/decrease", basket.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("decrease_basket",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_baskets").description("장바구니 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        pathParameters(
                                parameterWithName("id").description("개수 감소시킬 장바구니 id")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
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


        em.flush();
        em.clear();

        Basket findBasket = basketRepository.findById(basket.getId()).get();
        assertThat(findBasket.getAmount()).isEqualTo(amount - 1);

    }

    @Test
    @DisplayName("장바구니 물품 감소 시 1개면 더이상 감소하지않음")
    public void decreaseBasket_min_1() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        generateItemImage(item, 1);
        generateItemImage(item, 2);
        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        int amount = 1;
        Basket basket = generateBasket(member, item, amount);
        generateBasketOption(option1, basket, 1);
        generateBasketOption(option2, basket, 2);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/baskets/{id}/decrease", basket.getId())
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        Basket findBasket = basketRepository.findById(basket.getId()).get();
        assertThat(findBasket.getAmount()).isEqualTo(amount);

    }

    @Test
    @DisplayName("감소할 장바구니가 없음 404")
    public void decreaseBasket_404() throws Exception {
        //given
        Member member = memberRepository.findByEmail(appProperties.getUserEmail()).get();

        Item item = generateItem(1);
        generateItemImage(item, 1);
        generateItemImage(item, 2);
        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        int amount = 1;
        Basket basket = generateBasket(member, item, amount);
        generateBasketOption(option1, basket, 1);
        generateBasketOption(option2, basket, 2);

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/baskets/999999/decrease")
                        .header(HttpHeaders.AUTHORIZATION, getUserToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        Basket findBasket = basketRepository.findById(basket.getId()).get();
        assertThat(findBasket.getAmount()).isEqualTo(amount);

    }










    private Basket generateBasket(Member member, Item item, int amount) {
        Basket basket = Basket.builder()
                .item(item)
                .member(member)
                .amount(amount)
                .build();
        return basketRepository.save(basket);
    }


    private void generateBasketOption(ItemOption itemOption, Basket basket, int amount) {
        BasketOption basketOption = BasketOption.builder()
                .basket(basket)
                .itemOption(itemOption)
                .amount(amount)
                .build();
        basketOptionRepository.save(basketOption);
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

    private ItemImage generateItemImage(Item item, int i) {
        ItemImage itemImage = ItemImage.builder()
                .item(item)
                .leakOrder(i)
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        return itemImageRepository.save(itemImage);
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