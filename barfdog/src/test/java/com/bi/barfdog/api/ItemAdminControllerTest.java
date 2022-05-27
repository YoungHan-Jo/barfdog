package com.bi.barfdog.api;

import com.bi.barfdog.api.itemDto.ItemSaveDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.ItemContentImageRepository;
import com.bi.barfdog.repository.ItemImageRepository;
import com.bi.barfdog.repository.ItemOptionRepository;
import com.bi.barfdog.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ItemAdminControllerTest extends BaseTest {

    @Autowired
    EntityManager em;

    @Autowired
    AppProperties appProperties;

    @Autowired
    ItemContentImageRepository itemContentImageRepository;

    @Autowired
    ItemImageRepository itemImageRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemOptionRepository itemOptionRepository;

    
    @Test
    @DisplayName("정상적으로 아이템이미지 업로드하는 테스트")
    public void uploadItemImage() throws Exception {
       //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/admin/items/image/upload")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("url").exists())
                .andDo(document("upload_itemImage",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParts(
                                partWithName("file").description("업로드할 일반상품 이미지 파일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("일반상품 이미지 인덱스 id"),
                                fieldWithPath("url").description("이미지 display url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("업로드 할 이미지가 없을 경우 400")
    public void uploadItemImage_noFile_400() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/admin/items/image/upload")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 아이템 내용 이미지 업로드하는 테스트")
    public void uploadItemContentImage() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/admin/items/contentImage/upload")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("url").exists())
                .andDo(document("upload_itemContentImage",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParts(
                                partWithName("file").description("업로드할 일반상품 내용 이미지 파일")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("일반상품 내용 이미지 인덱스 id"),
                                fieldWithPath("url").description("이미지 display url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));
    }

    @Test
    @DisplayName("업로드할 아이템 내용 이미지 없을 경우 400")
    public void uploadItemContentImage_400() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/admin/items/contentImage/upload")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적으로 상품을 등록하는 테스트")
    public void createItem() throws Exception {
       //given
        List<ItemSaveDto.ItemOptionSaveDto> itemOptionSaveDtoList = new ArrayList<>();
        IntStream.range(1,4).forEach(i -> {
            addOption(itemOptionSaveDtoList, i);
        });

        List<Long> contentImageIdList = new ArrayList<>();
        ItemContentImage itemContentImage1 = generateItemContentImage(1);
        ItemContentImage itemContentImage2 = generateItemContentImage(2);
        ItemContentImage itemContentImage3 = generateItemContentImage(3);
        contentImageIdList.add(itemContentImage1.getId());
        contentImageIdList.add(itemContentImage2.getId());
        contentImageIdList.add(itemContentImage3.getId());

        List<ItemImage> itemImageList = new ArrayList<>();
        ItemImage itemImage1 = generateItemImage(1);
        ItemImage itemImage2 = generateItemImage(2);
        ItemImage itemImage3 = generateItemImage(3);
        itemImageList.add(itemImage1);
        itemImageList.add(itemImage2);
        itemImageList.add(itemImage3);

        List<ItemSaveDto.ItemImageOrderDto> itemImageOrderDtoList = new ArrayList<>();
        int order = 1;
        for (ItemImage itemImage : itemImageList) {
            ItemSaveDto.ItemImageOrderDto itemImageOrderDto = ItemSaveDto.ItemImageOrderDto.builder()
                    .id(itemImage.getId())
                    .leakOrder(order++)
                    .build();
            itemImageOrderDtoList.add(itemImageOrderDto);
        }

        String name = "상품 이름";
        String description = "상품 설명";
        String contents = "상세 내용";
        ItemSaveDto requestDto = ItemSaveDto.builder()
                .itemType(ItemType.RAW)
                .name(name)
                .description(description)
                .originalPrice(10000)
                .discountType(DiscountType.FIXED_RATE)
                .discountDegree(10)
                .salePrice(9000)
                .inStock(true)
                .remaining(9999)
                .contents(contents)
                .deliveryFree(true)
                .itemStatus(ItemStatus.LEAKED)
                .itemOptionSaveDtoList(itemOptionSaveDtoList)
                .contentImageIdList(contentImageIdList)
                .itemImageOrderDtoList(itemImageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("create_item",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("admin_query_items").description("상품 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestFields(
                                fieldWithPath("itemType").description("상품 타입 [RAW, TOPPING, GOODS]"),
                                fieldWithPath("name").description("상품 이름"),
                                fieldWithPath("description").description("상품 설명"),
                                fieldWithPath("originalPrice").description("원가"),
                                fieldWithPath("discountType").description("할인 타입 [FIXED_RATE, FLAT_RATE]"),
                                fieldWithPath("discountDegree").description("할인 정도"),
                                fieldWithPath("salePrice").description("할인적용 후 판매 가격"),
                                fieldWithPath("inStock").description("재고 여부 [true/false]"),
                                fieldWithPath("remaining").description("재고 수량"),
                                fieldWithPath("contents").description("상세 내용"),
                                fieldWithPath("deliveryFree").description("배송비 무료 여부 [true/false]"),
                                fieldWithPath("itemStatus").description("노출여부 [LEAKED,HIDDEN]"),
                                fieldWithPath("itemOptionSaveDtoList[0].name").description("옵션 이름"),
                                fieldWithPath("itemOptionSaveDtoList[0].price").description("옵션 가격"),
                                fieldWithPath("itemOptionSaveDtoList[0].remaining").description("옵션 재고 수량"),
                                fieldWithPath("contentImageIdList").description("상품 내용 이미지 id 리스트"),
                                fieldWithPath("itemImageOrderDtoList[0].id").description("상품 이미지 id"),
                                fieldWithPath("itemImageOrderDtoList[0].leakOrder").description("상품 이미지 노출 순서")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.admin_query_items.href").description("상품 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<Item> items = itemRepository.findByName(name);
        Item item = items.get(0);

        List<ItemOption> itemOptionList = itemOptionRepository.findByItem(item);
        assertThat(itemOptionList.size()).isEqualTo(3);

        List<ItemImage> itemImages = itemImageRepository.findByItemOrderByLeakOrderAsc(item);
        assertThat(itemImages.size()).isEqualTo(3);
        assertThat(itemImages.get(0).getLeakOrder()).isEqualTo(1);
        assertThat(itemImages.get(1).getLeakOrder()).isEqualTo(2);
        assertThat(itemImages.get(2).getLeakOrder()).isEqualTo(3);

        List<ItemContentImage> itemContentImages = itemContentImageRepository.findByItem(item);
        assertThat(itemContentImages.size()).isEqualTo(3);

    }

    @Test
    @DisplayName("상품을 등록 시 옵션 값 없어도 성공하는 테스트")
    public void createItem_noOption() throws Exception {
        //given

        List<Long> contentImageIdList = new ArrayList<>();
        ItemContentImage itemContentImage1 = generateItemContentImage(1);
        ItemContentImage itemContentImage2 = generateItemContentImage(2);
        ItemContentImage itemContentImage3 = generateItemContentImage(3);
        contentImageIdList.add(itemContentImage1.getId());
        contentImageIdList.add(itemContentImage2.getId());
        contentImageIdList.add(itemContentImage3.getId());

        List<ItemImage> itemImageList = new ArrayList<>();
        ItemImage itemImage1 = generateItemImage(1);
        ItemImage itemImage2 = generateItemImage(2);
        itemImageList.add(itemImage1);
        itemImageList.add(itemImage2);

        List<ItemSaveDto.ItemImageOrderDto> itemImageOrderDtoList = new ArrayList<>();
        int order = 1;
        for (ItemImage itemImage : itemImageList) {
            ItemSaveDto.ItemImageOrderDto itemImageOrderDto = ItemSaveDto.ItemImageOrderDto.builder()
                    .id(itemImage.getId())
                    .leakOrder(order++)
                    .build();
            itemImageOrderDtoList.add(itemImageOrderDto);
        }

        String name = "상품 이름";
        String description = "상품 설명";
        String contents = "상세 내용";
        ItemSaveDto requestDto = ItemSaveDto.builder()
                .itemType(ItemType.RAW)
                .name(name)
                .description(description)
                .originalPrice(10000)
                .discountType(DiscountType.FIXED_RATE)
                .discountDegree(10)
                .salePrice(9000)
                .inStock(true)
                .remaining(9999)
                .contents(contents)
                .deliveryFree(true)
                .itemStatus(ItemStatus.LEAKED)
                .contentImageIdList(contentImageIdList)
                .itemImageOrderDtoList(itemImageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        em.flush();
        em.clear();

        List<Item> items = itemRepository.findByName(name);
        Item item = items.get(0);

        List<ItemOption> itemOptionList = itemOptionRepository.findByItem(item);
        assertThat(itemOptionList.size()).isEqualTo(0);

        List<ItemImage> itemImages = itemImageRepository.findByItemOrderByLeakOrderAsc(item);
        assertThat(itemImages.size()).isEqualTo(2);
        assertThat(itemImages.get(0).getLeakOrder()).isEqualTo(1);
        assertThat(itemImages.get(1).getLeakOrder()).isEqualTo(2);

        List<ItemContentImage> itemContentImages = itemContentImageRepository.findByItem(item);
        assertThat(itemContentImages.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("상품을 등록 시 요청 값 부족 400")
    public void createItem_badRequest() throws Exception {
        //given

        List<Long> contentImageIdList = new ArrayList<>();
        ItemContentImage itemContentImage1 = generateItemContentImage(1);
        ItemContentImage itemContentImage2 = generateItemContentImage(2);
        ItemContentImage itemContentImage3 = generateItemContentImage(3);
        contentImageIdList.add(itemContentImage1.getId());
        contentImageIdList.add(itemContentImage2.getId());
        contentImageIdList.add(itemContentImage3.getId());

        List<ItemImage> itemImageList = new ArrayList<>();
        ItemImage itemImage1 = generateItemImage(1);
        ItemImage itemImage2 = generateItemImage(2);
        itemImageList.add(itemImage1);
        itemImageList.add(itemImage2);

        List<ItemSaveDto.ItemImageOrderDto> itemImageOrderDtoList = new ArrayList<>();
        int order = 1;
        for (ItemImage itemImage : itemImageList) {
            ItemSaveDto.ItemImageOrderDto itemImageOrderDto = ItemSaveDto.ItemImageOrderDto.builder()
                    .id(itemImage.getId())
                    .leakOrder(order++)
                    .build();
            itemImageOrderDtoList.add(itemImageOrderDto);
        }

        String name = "상품 이름";
        String description = "상품 설명";
        String contents = "상세 내용";
        ItemSaveDto requestDto = ItemSaveDto.builder()
                .itemType(ItemType.RAW)
                .originalPrice(10000)
                .discountType(DiscountType.FIXED_RATE)
                .discountDegree(10)
                .salePrice(9000)
                .inStock(true)
                .remaining(9999)
                .contents(contents)
                .deliveryFree(true)
                .itemStatus(ItemStatus.LEAKED)
                .contentImageIdList(contentImageIdList)
                .itemImageOrderDtoList(itemImageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("상품을 등록 시 존재하지않는 이미지 id일 경우 400")
    public void createItem_wrong_imageId() throws Exception {
        //given

        List<Long> contentImageIdList = new ArrayList<>();
        ItemContentImage itemContentImage1 = generateItemContentImage(1);
        ItemContentImage itemContentImage2 = generateItemContentImage(2);
        ItemContentImage itemContentImage3 = generateItemContentImage(3);
        contentImageIdList.add(itemContentImage1.getId());
        contentImageIdList.add(itemContentImage2.getId());
        contentImageIdList.add(itemContentImage3.getId());

        List<ItemSaveDto.ItemImageOrderDto> itemImageOrderDtoList = new ArrayList<>();
        itemImageOrderDtoList.add(ItemSaveDto.ItemImageOrderDto.builder()
                .id(100L)
                .leakOrder(1)
                .build());
        itemImageOrderDtoList.add(ItemSaveDto.ItemImageOrderDto.builder()
                .id(101L)
                .leakOrder(2)
                .build());


        String name = "상품 이름";
        String description = "상품 설명";
        String contents = "상세 내용";
        ItemSaveDto requestDto = ItemSaveDto.builder()
                .itemType(ItemType.RAW)
                .name(name)
                .description(description)
                .originalPrice(10000)
                .discountType(DiscountType.FIXED_RATE)
                .discountDegree(10)
                .salePrice(9000)
                .inStock(true)
                .remaining(9999)
                .contents(contents)
                .deliveryFree(true)
                .itemStatus(ItemStatus.LEAKED)
                .contentImageIdList(contentImageIdList)
                .itemImageOrderDtoList(itemImageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품을 등록 시 존재하지않는 내용 이미지 id일 경우 400")
    public void createItem_wrong_content_imageId() throws Exception {
        //given

        List<Long> contentImageIdList = new ArrayList<>();
        contentImageIdList.add(991L);
        contentImageIdList.add(992L);
        contentImageIdList.add(993L);

        List<ItemImage> itemImageList = new ArrayList<>();
        ItemImage itemImage1 = generateItemImage(1);
        ItemImage itemImage2 = generateItemImage(2);
        itemImageList.add(itemImage1);
        itemImageList.add(itemImage2);

        List<ItemSaveDto.ItemImageOrderDto> itemImageOrderDtoList = new ArrayList<>();
        int order = 1;
        for (ItemImage itemImage : itemImageList) {
            ItemSaveDto.ItemImageOrderDto itemImageOrderDto = ItemSaveDto.ItemImageOrderDto.builder()
                    .id(itemImage.getId())
                    .leakOrder(order++)
                    .build();
            itemImageOrderDtoList.add(itemImageOrderDto);
        }

        String name = "상품 이름";
        String description = "상품 설명";
        String contents = "상세 내용";
        ItemSaveDto requestDto = ItemSaveDto.builder()
                .itemType(ItemType.RAW)
                .name(name)
                .description(description)
                .originalPrice(10000)
                .discountType(DiscountType.FIXED_RATE)
                .discountDegree(10)
                .salePrice(9000)
                .inStock(true)
                .remaining(9999)
                .contents(contents)
                .deliveryFree(true)
                .itemStatus(ItemStatus.LEAKED)
                .contentImageIdList(contentImageIdList)
                .itemImageOrderDtoList(itemImageOrderDtoList)
                .build();

        //when & then
        mockMvc.perform(post("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
















    private ItemImage generateItemImage(int i) {
        ItemImage itemImage = ItemImage.builder()
                .folder("folder" + i)
                .filename("filename" + i +".jpg")
                .build();
        return itemImageRepository.save(itemImage);
    }

    private ItemContentImage generateItemContentImage(int i) {
        ItemContentImage itemContentImage = ItemContentImage.builder()
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        return itemContentImageRepository.save(itemContentImage);
    }

    private void addOption(List<ItemSaveDto.ItemOptionSaveDto> itemOptionSaveDtoList, int i) {
        ItemSaveDto.ItemOptionSaveDto optionSaveDto = ItemSaveDto.ItemOptionSaveDto.builder()
                .name("옵션" + i)
                .price(3000 + i*10)
                .remaining(9999)
                .build();
        itemOptionSaveDtoList.add(optionSaveDto);
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