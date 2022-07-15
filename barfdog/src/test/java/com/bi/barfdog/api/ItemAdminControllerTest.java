package com.bi.barfdog.api;

import com.bi.barfdog.api.itemDto.ItemSaveDto;
import com.bi.barfdog.api.itemDto.ItemUpdateDto;
import com.bi.barfdog.api.itemDto.QueryItemsAdminRequestDto;
import com.bi.barfdog.common.AppProperties;
import com.bi.barfdog.common.BaseTest;
import com.bi.barfdog.domain.coupon.DiscountType;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.jwt.JwtLoginDto;
import com.bi.barfdog.repository.delivery.DeliveryRepository;
import com.bi.barfdog.repository.item.ItemContentImageRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
import com.bi.barfdog.repository.order.OrderRepository;
import com.bi.barfdog.repository.orderItem.OrderItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    DeliveryRepository deliveryRepository;

    @Before
    public void setUp() {

        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        itemImageRepository.deleteAll();
        itemOptionRepository.deleteAll();
        itemRepository.deleteAll();
        deliveryRepository.deleteAll();

    }

    
    @Test
    @DisplayName("정상적으로 아이템이미지 업로드하는 테스트")
    public void uploadItemImage() throws Exception {
       //given

        MockMultipartFile file = new MockMultipartFile("file", "file1.jpg", "image/jpg", new FileInputStream("src/test/resources/uploadTest/file1.jpg"));

        //when & then
        mockMvc.perform(multipart("/api/admin/items/image/upload")
                        .file(file)
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
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
                        .contentType(MediaType.MULTIPART_FORM_DATA)
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
        String itemIcons = "BEST,NEW";
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
                .itemIcons(itemIcons)
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
                                fieldWithPath("itemIcons").description("상품 아이콘 (공백 없이/내용 없으면 빈 문자열) ['' or 'BEST' or 'NEW' or 'BEST,NEW']"),
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
        assertThat(item.getItemIcons()).isEqualTo(itemIcons);
        assertThat(item.getTotalSalesAmount()).isEqualTo(0);

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
        DiscountType discountType = DiscountType.FIXED_RATE;
        ItemSaveDto requestDto = ItemSaveDto.builder()
                .itemType(ItemType.RAW)
                .name(name)
                .description(description)
                .originalPrice(10000)
                .discountType(discountType)
                .discountDegree(10)
                .salePrice(9000)
                .inStock(true)
                .remaining(9999)
                .contents(contents)
                .itemIcons("BEST,NEW")
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
        assertThat(item.getDiscountType()).isEqualTo(discountType);

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
    @DisplayName("상품을 등록 시 FLAT 타입으로 등록하는 테스트")
    public void createItem_noOption_Flat() throws Exception {
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
        DiscountType discountType = DiscountType.FLAT_RATE;
        ItemSaveDto requestDto = ItemSaveDto.builder()
                .itemType(ItemType.RAW)
                .name(name)
                .description(description)
                .originalPrice(10000)
                .discountType(discountType)
                .discountDegree(10)
                .salePrice(9000)
                .inStock(true)
                .remaining(9999)
                .contents(contents)
                .itemIcons("BEST,NEW")
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
        assertThat(item.getDiscountType()).isEqualTo(discountType);

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

    @Test
    @DisplayName("수정할 아이템 하나 조회하는 테스트")
    public void queryItem() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        IntStream.range(1,4).forEach(i -> {
            generateOption(item, i);
            generateItemImage(item, i);
            generateItemContentImage(item, i);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/items/{id}", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_item",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_items").description("일반상품 리스트 조회 링크"),
                                linkWithRel("upload_itemImages").description("일반상품 리스트 조회 링크"),
                                linkWithRel("upload_itemContentImages").description("일반상품 리스트 조회 링크"),
                                linkWithRel("update_item").description("일반상품 수정 요청 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("조회할 상품 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("itemAdminDto.id").description("일반 상품 인덱스 id"),
                                fieldWithPath("itemAdminDto.itemType").description("상품 타입 [RAW, TOPPING, GOODS]"),
                                fieldWithPath("itemAdminDto.name").description("상품 이름"),
                                fieldWithPath("itemAdminDto.description").description("상품 설명"),
                                fieldWithPath("itemAdminDto.originalPrice").description("상품 기존 가격"),
                                fieldWithPath("itemAdminDto.discountType").description("할인 타입 [FIXED_RATE, FLAT_RATE]"),
                                fieldWithPath("itemAdminDto.discountDegree").description("할인 정도"),
                                fieldWithPath("itemAdminDto.salePrice").description("할인 적용 후 판매 가격"),
                                fieldWithPath("itemAdminDto.inStock").description("재고 여부 true/false"),
                                fieldWithPath("itemAdminDto.remaining").description("잔여 수량"),
                                fieldWithPath("itemAdminDto.contents").description("상세 내용"),
                                fieldWithPath("itemAdminDto.itemIcons").description("상품 아이콘 (공백 없이/내용 없으면 빈 문자열) ex)['' or 'BEST' or 'NEW' or 'BEST,NEW']"),
                                fieldWithPath("itemAdminDto.deliveryFree").description("배송비 무료 여부 true/false"),
                                fieldWithPath("itemAdminDto.status").description("상품 노출 상태 [LEAKED,HIDDEN]"),
                                fieldWithPath("itemOptionAdminDtoList[0].id").description("상품 옵션 인덱스 id"),
                                fieldWithPath("itemOptionAdminDtoList[0].name").description("옵션 이름"),
                                fieldWithPath("itemOptionAdminDtoList[0].optionPrice").description("옵션 추가 금액"),
                                fieldWithPath("itemOptionAdminDtoList[0].remaining").description("옵션 재고수량"),
                                fieldWithPath("itemImageAdminDtoList[0].id").description("상품 이미지 인덱스 id"),
                                fieldWithPath("itemImageAdminDtoList[0].leakOrder").description("상품 이미지 순서"),
                                fieldWithPath("itemImageAdminDtoList[0].filename").description("상품 이미지 파일이름"),
                                fieldWithPath("itemImageAdminDtoList[0].url").description("상품 이미지 url"),
                                fieldWithPath("itemContentImageDtoList[0].id").description("내용 이미지 인덱스 id"),
                                fieldWithPath("itemContentImageDtoList[0].filename").description("내용 이미지 파일 이름"),
                                fieldWithPath("itemContentImageDtoList[0].url").description("내용 이미지 url"),
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_items.href").description("일반상품 리스트 조회 링크"),
                                fieldWithPath("_links.upload_itemImages.href").description("상품 이미지 업로드 링크"),
                                fieldWithPath("_links.upload_itemContentImages.href").description("상품 내용 이미지 업로드 링크"),
                                fieldWithPath("_links.update_item.href").description("일반상품 수정 요청 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

    }

    @Test
    @DisplayName("존재하지 않는 아이템을 경우 404 나오는 테스트")
    public void queryItem_notFound() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        IntStream.range(1,4).forEach(i -> {
            generateOption(item, i);
            generateItemImage(item, i);
            generateItemContentImage(item, i);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/admin/items/99999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("정상적으로 상품 리스트 조회하는 테스트")
    public void queryItems() throws Exception {
       //given
        IntStream.range(1,7).forEach(i -> {
            Item item = generateItem(i, DiscountType.FLAT_RATE, 1000);
            if (i % 2 == 0) {
                generateOption(item, i);
            }
        });

        IntStream.range(7,14).forEach(i -> {
            Item item = generateItem(i, DiscountType.FIXED_RATE, 10);
            if (i % 2 == 0) {
                generateOption(item, i);
            }
        });

        //when & then
        mockMvc.perform(get("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size","5")
                        .param("page","1")
                        .param("itemType","RAW"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_query_items",
                        links(
                                linkWithRel("first").description("첫 페이지 링크"),
                                linkWithRel("prev").description("이전 페이지 링크"),
                                linkWithRel("self").description("현재 페이지 링크"),
                                linkWithRel("next").description("다음 페이지 링크"),
                                linkWithRel("last").description("마지막 페이지 링크"),
                                linkWithRel("create_item").description("상품 생성 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지 번호 [0번부터 시작 - 0번이 첫 페이지]"),
                                parameterWithName("size").description("한 페이지 당 조회 개수"),
                                parameterWithName("itemType").description("상품 카테고리 [RAW, TOPPING, GOODS]")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].id").description("상품 인덱스 id"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].name").description("상품 이름"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].itemType").description("상품 타입 [RAW, TOPPING, GOODS]"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].itemIcons").description("상품 아이콘 (공백 없이/내용 없으면 빈 문자열) ex)['' or 'BEST' or 'NEW' or 'BEST,NEW']"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].option").description("상품 옵션 존재 여부 true/false"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].originalPrice").description("원가"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].discount").description("할인"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].salePrice").description("할인 적용 후 판매가"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].status").description("상품 노출 상태 [LEAKED,HIDDEN]"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].remaining").description("재고 수량"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0].createdDate").description("생성 날짜"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0]._links.query_item.href").description("해당 상품 조회 링크"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0]._links.update_item.href").description("해당 상품 수정 링크"),
                                fieldWithPath("_embedded.queryItemsAdminDtoList[0]._links.delete_item.href").description("해당 상품 삭제 링크"),
                                fieldWithPath("page.size").description("한 페이지 당 개수"),
                                fieldWithPath("page.totalElements").description("검색 총 결과 수"),
                                fieldWithPath("page.totalPages").description("총 페이지 수"),
                                fieldWithPath("page.number").description("페이지 번호 [0페이지 부터 시작]"),
                                fieldWithPath("_links.first.href").description("첫 페이지 링크"),
                                fieldWithPath("_links.prev.href").description("이전 페이지 링크"),
                                fieldWithPath("_links.self.href").description("현재 페이지 링크"),
                                fieldWithPath("_links.next.href").description("다음 페이지 링크"),
                                fieldWithPath("_links.last.href").description("마지막 페이지 링크"),
                                fieldWithPath("_links.create_item.href").description("상품 생성 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ))
        ;

        em.flush();
        em.clear();

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList.size()).isEqualTo(13);

    }

    @Test
    @DisplayName("정상적으로 상품 리스트 topping 만 조회하는 테스트")
    public void queryItems_HIDDEN() throws Exception {
        //given
        IntStream.range(1,7).forEach(i -> {
            generateItemTopping(i, ItemStatus.LEAKED);
            generateItemTopping(i, ItemStatus.HIDDEN);
        });
        IntStream.range(1,5).forEach(i -> {
            generateItemGoods(i);
            generateItem(i, DiscountType.FLAT_RATE, 5);
        });


        //when & then
        mockMvc.perform(get("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size","5")
                        .param("page","1")
                        .param("itemType","TOPPING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(12))
        ;

        em.flush();
        em.clear();

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList.size()).isEqualTo(20);
    }

    @Test
    @DisplayName("정상적으로 상품 리스트 topping 만 조회하는 테스트")
    public void queryItems_topping() throws Exception {
        //given
        IntStream.range(1,7).forEach(i -> {
            generateItemTopping(i, ItemStatus.LEAKED);
        });
        IntStream.range(1,3).forEach(i -> {
            generateItemGoods(i);
        });


        //when & then
        mockMvc.perform(get("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size","5")
                        .param("page","1")
                        .param("itemType","TOPPING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(6))
        ;

        em.flush();
        em.clear();

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList.size()).isEqualTo(8);

    }

    @Test
    @DisplayName("정상적으로 상품 리스트 goods 만 조회하는 테스트")
    public void queryItems_goods() throws Exception {
        //given
        IntStream.range(1,7).forEach(i -> {
            generateItemTopping(i, ItemStatus.LEAKED);
        });
        IntStream.range(1,3).forEach(i -> {
            generateItemGoods(i);
        });

        QueryItemsAdminRequestDto requestDto = QueryItemsAdminRequestDto.builder()
                .itemType(ItemType.GOODS)
                .build();

        //when & then
        mockMvc.perform(get("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("size","5")
                        .param("page","0")
                        .param("itemType","GOODS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page.totalElements").value(2))
        ;

        em.flush();
        em.clear();

        List<Item> itemList = itemRepository.findAll();
        assertThat(itemList.size()).isEqualTo(8);

    }

    @Test
    @DisplayName("상품 리스트 조회 시 페이지 정보 없을 경우 0페이지 20개 조회")
    public void queryItems_noPage() throws Exception {
        //given
        IntStream.range(1,24).forEach(i -> {
            Item item = generateItem(i, DiscountType.FLAT_RATE, 1000);
            if (i % 2 == 0) {
                generateOption(item, i);
            }
        });

        //when & then
        mockMvc.perform(get("/api/admin/items")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .param("itemType","RAW"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryItemsAdminDtoList", hasSize(20)))
                .andExpect(jsonPath("page.number").value(0))
        ;
    }

    @Test
    @DisplayName("정상적으로 상품을 수정하는 테스트")
    public void updateItem() throws Exception {
       //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);
        ItemOption deleteOption1 = generateOption(item, 3);
        ItemOption deleteOption2 = generateOption(item, 4);

        ItemImage itemImage1 = generateItemImage(item, 1);
        ItemImage deleteItemImage1 = generateItemImage(item, 2);
        ItemImage deleteItemImage2 = generateItemImage(item, 3);

        ItemContentImage itemContentImage1 = generateItemContentImage(item, 1);
        ItemContentImage deleteItemContentImage1 = generateItemContentImage(item, 2);
        ItemContentImage deleteItemContentImage2 = generateItemContentImage(item, 3);

        List<Long> addContentImageIdList = new ArrayList<>();
        IntStream.range(11,14).forEach(i -> {
            ItemContentImage itemContentImage = generateItemContentImage(i);
            addContentImageIdList.add(itemContentImage.getId());
        });

        List<Long> deleteContentImageIdList = new ArrayList<>();
        deleteContentImageIdList.add(deleteItemContentImage1.getId());
        deleteContentImageIdList.add(deleteItemContentImage2.getId());

        List<Long> deleteOptionIdList = new ArrayList<>();
        deleteOptionIdList.add(deleteOption1.getId());
        deleteOptionIdList.add(deleteOption2.getId());

        List<ItemUpdateDto.ItemOptionSaveDto> itemOptionSaveDtoList = new ArrayList<>();

        IntStream.range(11,14).forEach(i -> {
            ItemUpdateDto.ItemOptionSaveDto itemOptionSaveDto = generateOption(i);
            itemOptionSaveDtoList.add(itemOptionSaveDto);
        });

        List<ItemUpdateDto.ItemOptionUpdateDto> itemOptionUpdateDtoList = new ArrayList<>();
        addOptionUpdateDtoInList(option1, itemOptionUpdateDtoList);
        addOptionUpdateDtoInList(option2, itemOptionUpdateDtoList);

        List<Long> deleteImageIdList = new ArrayList<>();
        deleteImageIdList.add(deleteItemImage1.getId());
        deleteImageIdList.add(deleteItemImage2.getId());

        List<Long> addImageIdList = new ArrayList<>();
        IntStream.range(11,14).forEach(i -> {
            ItemImage itemImage = generateItemImage(i);
            addImageIdList.add(itemImage.getId());
        });

        List<ItemUpdateDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        addImageOrderDtoList(imageOrderDtoList, itemImage1.getId(), 1);
        addImageOrderDtoList(imageOrderDtoList, addImageIdList.get(0), 2);
        addImageOrderDtoList(imageOrderDtoList, addImageIdList.get(1), 3);
        addImageOrderDtoList(imageOrderDtoList, addImageIdList.get(2), 4);


        String name = "수정한 이름";
        String description = "수정한 설명";
        int originalPrice = 20000;
        String contents = "수정된 내용";
        ItemStatus status = ItemStatus.HIDDEN;
        boolean deliveryFree = false;
        boolean inStock = false;
        int remaining = 0;
        DiscountType discountType = DiscountType.FLAT_RATE;
        ItemType itemType = ItemType.TOPPING;
        int discountDegree = 1000;
        int salePrice = 19000;
        String itemIcons = "";
        ItemUpdateDto requestDto = ItemUpdateDto.builder()
                .itemType(itemType)
                .name(name)
                .description(description)
                .originalPrice(originalPrice)
                .discountType(discountType)
                .discountDegree(discountDegree)
                .salePrice(salePrice)
                .inStock(inStock)
                .remaining(remaining)
                .contents(contents)
                .itemIcons(itemIcons)
                .deliveryFree(deliveryFree)
                .itemStatus(status)
                .addContentImageIdList(addContentImageIdList)
                .deleteContentImageIdList(deleteContentImageIdList)
                .deleteOptionIdList(deleteOptionIdList)
                .itemOptionSaveDtoList(itemOptionSaveDtoList)
                .itemOptionUpdateDtoList(itemOptionUpdateDtoList)
                .deleteImageIdList(deleteImageIdList)
                .addImageIdList(addImageIdList)
                .imageOrderDtoList(imageOrderDtoList)
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/items/{id}", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_update_item",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_items").description("상품 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("수정할 상품 인덱스 id")
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
                                fieldWithPath("itemIcons").description("상품 아이콘 (공백 없이/내용 없으면 빈 문자열) ['' or 'BEST' or 'NEW' or 'BEST,NEW']"),
                                fieldWithPath("deliveryFree").description("배송비 무료 여부 [true/false]"),
                                fieldWithPath("itemStatus").description("노출여부 [LEAKED,HIDDEN]"),
                                fieldWithPath("addContentImageIdList").description("추가할 내용 이미지 인덱스 id 리스트"),
                                fieldWithPath("deleteContentImageIdList").description("삭제할 내용 이미지 인덱스 id 리스트"),
                                fieldWithPath("deleteOptionIdList").description("삭제할 상품 옵션 인덱스 id 리스트"),
                                fieldWithPath("itemOptionSaveDtoList[0].name").description("추가할 옵션의 이름"),
                                fieldWithPath("itemOptionSaveDtoList[0].price").description("추가할 옵션의 가격"),
                                fieldWithPath("itemOptionSaveDtoList[0].remaining").description("추가할 옵션의 재고량"),
                                fieldWithPath("itemOptionUpdateDtoList[0].id").description("수정할 옵션의 인덱스 id"),
                                fieldWithPath("itemOptionUpdateDtoList[0].name").description("수정할 옵션의 이름"),
                                fieldWithPath("itemOptionUpdateDtoList[0].price").description("수정할 옵션의 가격"),
                                fieldWithPath("itemOptionUpdateDtoList[0].remaining").description("수정할 옵션의 재고량"),
                                fieldWithPath("deleteImageIdList").description("삭제할 상품 이미지 인덱스 id 리스트"),
                                fieldWithPath("addImageIdList").description("추가할 상품 이미지 인덱스 id 리스트"),
                                fieldWithPath("imageOrderDtoList[0].id").description("상품 이미지 인덱스 id"),
                                fieldWithPath("imageOrderDtoList[0].leakOrder").description("상품 이미지 노출 순서")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_items.href").description("상품 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        List<ItemOption> allOptions = itemOptionRepository.findByItem(item);
        assertThat(allOptions.size()).isEqualTo(5);

        List<ItemImage> allItemImages = itemImageRepository.findByItem(item);
        assertThat(allItemImages.size()).isEqualTo(4);
        assertThat(allItemImages.get(0).getLeakOrder()).isEqualTo(1);
        assertThat(allItemImages.get(1).getLeakOrder()).isEqualTo(2);
        assertThat(allItemImages.get(2).getLeakOrder()).isEqualTo(3);
        assertThat(allItemImages.get(3).getLeakOrder()).isEqualTo(4);

        List<ItemContentImage> allItemContentImages = itemContentImageRepository.findByItem(item);
        assertThat(allItemContentImages.size()).isEqualTo(4);

        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem.getName()).isEqualTo(name);
        assertThat(findItem.getDescription()).isEqualTo(description);
        assertThat(findItem.getOriginalPrice()).isEqualTo(originalPrice);
        assertThat(findItem.getContents()).isEqualTo(contents);
        assertThat(findItem.getStatus()).isEqualTo(status);
        assertThat(findItem.getItemIcons()).isEqualTo(itemIcons);
        assertThat(findItem.isDeliveryFree()).isEqualTo(deliveryFree);
        assertThat(findItem.isInStock()).isEqualTo(inStock);
        assertThat(findItem.getRemaining()).isEqualTo(remaining);
        assertThat(findItem.getDiscountType()).isEqualTo(discountType);
        assertThat(findItem.getItemType()).isEqualTo(itemType);
        assertThat(findItem.getDiscountDegree()).isEqualTo(discountDegree);
        assertThat(findItem.getSalePrice()).isEqualTo(salePrice);
        assertThat(findItem.getTotalSalesAmount()).isEqualTo(0);
    }

    @Test
    @DisplayName("옵션 변화 없어도 정상적으로 상품을 수정하는 테스트")
    public void updateItem_option_unchanged() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        ItemImage itemImage1 = generateItemImage(item, 1);
        ItemImage deleteItemImage1 = generateItemImage(item, 2);
        ItemImage deleteItemImage2 = generateItemImage(item, 3);

        ItemContentImage itemContentImage1 = generateItemContentImage(item, 1);
        ItemContentImage deleteItemContentImage1 = generateItemContentImage(item, 2);
        ItemContentImage deleteItemContentImage2 = generateItemContentImage(item, 3);

        List<Long> addContentImageIdList = new ArrayList<>();
        IntStream.range(11,14).forEach(i -> {
            ItemContentImage itemContentImage = generateItemContentImage(i);
            addContentImageIdList.add(itemContentImage.getId());
        });

        List<Long> deleteContentImageIdList = new ArrayList<>();
        deleteContentImageIdList.add(deleteItemContentImage1.getId());
        deleteContentImageIdList.add(deleteItemContentImage2.getId());


        List<Long> deleteImageIdList = new ArrayList<>();
        deleteImageIdList.add(deleteItemImage1.getId());
        deleteImageIdList.add(deleteItemImage2.getId());

        List<Long> addImageIdList = new ArrayList<>();
        IntStream.range(11,14).forEach(i -> {
            ItemImage itemImage = generateItemImage(i);
            addImageIdList.add(itemImage.getId());
        });

        List<ItemUpdateDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        addImageOrderDtoList(imageOrderDtoList, itemImage1.getId(), 1);
        addImageOrderDtoList(imageOrderDtoList, addImageIdList.get(0), 2);
        addImageOrderDtoList(imageOrderDtoList, addImageIdList.get(1), 3);
        addImageOrderDtoList(imageOrderDtoList, addImageIdList.get(2), 4);


        String name = "수정한 이름";
        String description = "수정한 설명";
        int originalPrice = 20000;
        String contents = "수정된 내용";
        ItemStatus status = ItemStatus.HIDDEN;
        boolean deliveryFree = false;
        boolean inStock = false;
        int remaining = 0;
        DiscountType discountType = DiscountType.FLAT_RATE;
        ItemType itemType = ItemType.TOPPING;
        int discountDegree = 1000;
        int salePrice = 19000;
        ItemUpdateDto requestDto = ItemUpdateDto.builder()
                .itemType(itemType)
                .name(name)
                .description(description)
                .originalPrice(originalPrice)
                .discountType(discountType)
                .discountDegree(discountDegree)
                .salePrice(salePrice)
                .inStock(inStock)
                .remaining(remaining)
                .contents(contents)
                .itemIcons("")
                .deliveryFree(deliveryFree)
                .itemStatus(status)
                .addContentImageIdList(addContentImageIdList)
                .deleteContentImageIdList(deleteContentImageIdList)
                .deleteImageIdList(deleteImageIdList)
                .addImageIdList(addImageIdList)
                .imageOrderDtoList(imageOrderDtoList)
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/items/{id}", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        List<ItemOption> allOptions = itemOptionRepository.findByItem(item);
        assertThat(allOptions.size()).isEqualTo(2);

        List<ItemImage> allItemImages = itemImageRepository.findByItem(item);
        assertThat(allItemImages.size()).isEqualTo(4);
        assertThat(allItemImages.get(0).getLeakOrder()).isEqualTo(1);
        assertThat(allItemImages.get(1).getLeakOrder()).isEqualTo(2);
        assertThat(allItemImages.get(2).getLeakOrder()).isEqualTo(3);
        assertThat(allItemImages.get(3).getLeakOrder()).isEqualTo(4);

        List<ItemContentImage> allItemContentImages = itemContentImageRepository.findByItem(item);
        assertThat(allItemContentImages.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("이미지 변화 없어도 정상적으로 상품을 수정하는 테스트")
    public void updateItem_image_unchanged() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        ItemImage itemImage1 = generateItemImage(item, 1);

        ItemContentImage itemContentImage1 = generateItemContentImage(item, 1);
        ItemContentImage deleteItemContentImage1 = generateItemContentImage(item, 2);
        ItemContentImage deleteItemContentImage2 = generateItemContentImage(item, 3);

        List<Long> addContentImageIdList = new ArrayList<>();
        IntStream.range(11,14).forEach(i -> {
            ItemContentImage itemContentImage = generateItemContentImage(i);
            addContentImageIdList.add(itemContentImage.getId());
        });

        List<Long> deleteContentImageIdList = new ArrayList<>();
        deleteContentImageIdList.add(deleteItemContentImage1.getId());
        deleteContentImageIdList.add(deleteItemContentImage2.getId());

        List<ItemUpdateDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        addImageOrderDtoList(imageOrderDtoList, itemImage1.getId(), 1);


        String name = "수정한 이름";
        String description = "수정한 설명";
        int originalPrice = 20000;
        String contents = "수정된 내용";
        ItemStatus status = ItemStatus.HIDDEN;
        boolean deliveryFree = false;
        boolean inStock = false;
        int remaining = 0;
        DiscountType discountType = DiscountType.FLAT_RATE;
        ItemType itemType = ItemType.TOPPING;
        int discountDegree = 1000;
        int salePrice = 19000;
        ItemUpdateDto requestDto = ItemUpdateDto.builder()
                .itemType(itemType)
                .name(name)
                .description(description)
                .originalPrice(originalPrice)
                .discountType(discountType)
                .discountDegree(discountDegree)
                .salePrice(salePrice)
                .inStock(inStock)
                .remaining(remaining)
                .contents(contents)
                .itemIcons("")
                .deliveryFree(deliveryFree)
                .itemStatus(status)
                .addContentImageIdList(addContentImageIdList)
                .deleteContentImageIdList(deleteContentImageIdList)
                .imageOrderDtoList(imageOrderDtoList)
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/items/{id}", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        List<ItemOption> allOptions = itemOptionRepository.findByItem(item);
        assertThat(allOptions.size()).isEqualTo(2);

        List<ItemImage> allItemImages = itemImageRepository.findByItem(item);
        assertThat(allItemImages.size()).isEqualTo(1);
        assertThat(allItemImages.get(0).getLeakOrder()).isEqualTo(1);

        List<ItemContentImage> allItemContentImages = itemContentImageRepository.findByItem(item);
        assertThat(allItemContentImages.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("내용 이미지 추가만 있어도 정상적으로 상품을 수정하는 테스트")
    public void updateItem_content_image_AddOnly() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        ItemImage itemImage1 = generateItemImage(item, 1);

        ItemContentImage itemContentImage1 = generateItemContentImage(item, 1);

        List<Long> addContentImageIdList = new ArrayList<>();
        IntStream.range(11,14).forEach(i -> {
            ItemContentImage itemContentImage = generateItemContentImage(i);
            addContentImageIdList.add(itemContentImage.getId());
        });

        List<ItemUpdateDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        addImageOrderDtoList(imageOrderDtoList, itemImage1.getId(), 1);


        String name = "수정한 이름";
        String description = "수정한 설명";
        int originalPrice = 20000;
        String contents = "수정된 내용";
        ItemStatus status = ItemStatus.HIDDEN;
        boolean deliveryFree = false;
        boolean inStock = false;
        int remaining = 0;
        DiscountType discountType = DiscountType.FLAT_RATE;
        ItemType itemType = ItemType.TOPPING;
        int discountDegree = 1000;
        int salePrice = 19000;
        ItemUpdateDto requestDto = ItemUpdateDto.builder()
                .itemType(itemType)
                .name(name)
                .description(description)
                .originalPrice(originalPrice)
                .discountType(discountType)
                .discountDegree(discountDegree)
                .salePrice(salePrice)
                .inStock(inStock)
                .remaining(remaining)
                .contents(contents)
                .itemIcons("")
                .deliveryFree(deliveryFree)
                .itemStatus(status)
                .addContentImageIdList(addContentImageIdList)
                .imageOrderDtoList(imageOrderDtoList)
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/items/{id}", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        List<ItemOption> allOptions = itemOptionRepository.findByItem(item);
        assertThat(allOptions.size()).isEqualTo(2);

        List<ItemImage> allItemImages = itemImageRepository.findByItem(item);
        assertThat(allItemImages.size()).isEqualTo(1);
        assertThat(allItemImages.get(0).getLeakOrder()).isEqualTo(1);

        List<ItemContentImage> allItemContentImages = itemContentImageRepository.findByItem(item);
        assertThat(allItemContentImages.size()).isEqualTo(4);

    }

    @Test
    @DisplayName("내용 이미지 변화 없어도 정상적으로 상품을 수정하는 테스트")
    public void updateItem_content_image_unchanged() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        ItemImage itemImage1 = generateItemImage(item, 1);

        ItemContentImage itemContentImage1 = generateItemContentImage(item, 1);

        List<ItemUpdateDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        addImageOrderDtoList(imageOrderDtoList, itemImage1.getId(), 1);

        String name = "수정한 이름";
        String description = "수정한 설명";
        int originalPrice = 20000;
        String contents = "수정된 내용";
        ItemStatus status = ItemStatus.HIDDEN;
        boolean deliveryFree = false;
        boolean inStock = false;
        int remaining = 0;
        DiscountType discountType = DiscountType.FLAT_RATE;
        ItemType itemType = ItemType.TOPPING;
        int discountDegree = 1000;
        int salePrice = 19000;
        ItemUpdateDto requestDto = ItemUpdateDto.builder()
                .itemType(itemType)
                .name(name)
                .description(description)
                .originalPrice(originalPrice)
                .discountType(discountType)
                .discountDegree(discountDegree)
                .salePrice(salePrice)
                .inStock(inStock)
                .itemIcons("")
                .remaining(remaining)
                .contents(contents)
                .deliveryFree(deliveryFree)
                .itemStatus(status)
                .imageOrderDtoList(imageOrderDtoList)
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/items/{id}", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;

        em.flush();
        em.clear();

        List<ItemOption> allOptions = itemOptionRepository.findByItem(item);
        assertThat(allOptions.size()).isEqualTo(2);

        List<ItemImage> allItemImages = itemImageRepository.findByItem(item);
        assertThat(allItemImages.size()).isEqualTo(1);
        assertThat(allItemImages.get(0).getLeakOrder()).isEqualTo(1);

        List<ItemContentImage> allItemContentImages = itemContentImageRepository.findByItem(item);
        assertThat(allItemContentImages.size()).isEqualTo(1);

    }

    @Test
    @DisplayName("상품 수정 시 값 부족할 경우 400")
    public void updateItem_bad_request() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        ItemImage itemImage1 = generateItemImage(item, 1);

        ItemContentImage itemContentImage1 = generateItemContentImage(item, 1);

        String name = "수정한 이름";
        String description = "수정한 설명";
        int originalPrice = 20000;
        String contents = "수정된 내용";
        ItemStatus status = ItemStatus.HIDDEN;
        boolean deliveryFree = false;
        boolean inStock = false;
        int remaining = 0;
        DiscountType discountType = DiscountType.FLAT_RATE;
        ItemType itemType = ItemType.TOPPING;
        int discountDegree = 1000;
        int salePrice = 19000;
        ItemUpdateDto requestDto = ItemUpdateDto.builder()
                .itemType(itemType)
                .name(name)
                .discountType(discountType)
                .discountDegree(discountDegree)
                .salePrice(salePrice)
                .inStock(inStock)
                .remaining(remaining)
                .contents(contents)
                .deliveryFree(deliveryFree)
                .itemStatus(status)
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/items/{id}", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @DisplayName("상품 수정 시 이미지 파일 순서 정보 없을 시 400")
    public void updateItem_no_imageLeakOrder_400() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);

        ItemImage itemImage1 = generateItemImage(item, 1);

        ItemContentImage itemContentImage1 = generateItemContentImage(item, 1);


        String name = "수정한 이름";
        String description = "수정한 설명";
        int originalPrice = 20000;
        String contents = "수정된 내용";
        ItemStatus status = ItemStatus.HIDDEN;
        boolean deliveryFree = false;
        boolean inStock = false;
        int remaining = 0;
        DiscountType discountType = DiscountType.FLAT_RATE;
        ItemType itemType = ItemType.TOPPING;
        int discountDegree = 1000;
        int salePrice = 19000;
        ItemUpdateDto requestDto = ItemUpdateDto.builder()
                .itemType(itemType)
                .name(name)
                .description(description)
                .originalPrice(originalPrice)
                .discountType(discountType)
                .discountDegree(discountDegree)
                .salePrice(salePrice)
                .inStock(inStock)
                .remaining(remaining)
                .contents(contents)
                .deliveryFree(deliveryFree)
                .itemStatus(status)
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/items/{id}", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }


    @Test
    @DisplayName("수정할 상품이 존재하지 않을 경우 404 에러")
    public void updateItem_notFound() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        ItemOption option1 = generateOption(item, 1);
        ItemOption option2 = generateOption(item, 2);
        ItemOption deleteOption1 = generateOption(item, 3);
        ItemOption deleteOption2 = generateOption(item, 4);

        ItemImage itemImage1 = generateItemImage(item, 1);
        ItemImage deleteItemImage1 = generateItemImage(item, 2);
        ItemImage deleteItemImage2 = generateItemImage(item, 3);

        ItemContentImage itemContentImage1 = generateItemContentImage(item, 1);
        ItemContentImage deleteItemContentImage1 = generateItemContentImage(item, 2);
        ItemContentImage deleteItemContentImage2 = generateItemContentImage(item, 3);

        List<Long> addContentImageIdList = new ArrayList<>();
        IntStream.range(11,14).forEach(i -> {
            ItemContentImage itemContentImage = generateItemContentImage(i);
            addContentImageIdList.add(itemContentImage.getId());
        });

        List<Long> deleteContentImageIdList = new ArrayList<>();
        deleteContentImageIdList.add(deleteItemContentImage1.getId());
        deleteContentImageIdList.add(deleteItemContentImage2.getId());

        List<Long> deleteOptionIdList = new ArrayList<>();
        deleteOptionIdList.add(deleteOption1.getId());
        deleteOptionIdList.add(deleteOption2.getId());

        List<ItemUpdateDto.ItemOptionSaveDto> itemOptionSaveDtoList = new ArrayList<>();

        IntStream.range(11,14).forEach(i -> {
            ItemUpdateDto.ItemOptionSaveDto itemOptionSaveDto = generateOption(i);
            itemOptionSaveDtoList.add(itemOptionSaveDto);
        });

        List<ItemUpdateDto.ItemOptionUpdateDto> itemOptionUpdateDtoList = new ArrayList<>();
        addOptionUpdateDtoInList(option1, itemOptionUpdateDtoList);
        addOptionUpdateDtoInList(option2, itemOptionUpdateDtoList);

        List<Long> deleteImageIdList = new ArrayList<>();
        deleteImageIdList.add(deleteItemImage1.getId());
        deleteImageIdList.add(deleteItemImage2.getId());

        List<Long> addImageIdList = new ArrayList<>();
        IntStream.range(11,14).forEach(i -> {
            ItemImage itemImage = generateItemImage(i);
            addImageIdList.add(itemImage.getId());
        });

        List<ItemUpdateDto.ImageOrderDto> imageOrderDtoList = new ArrayList<>();
        addImageOrderDtoList(imageOrderDtoList, itemImage1.getId(), 1);
        addImageOrderDtoList(imageOrderDtoList, addImageIdList.get(0), 2);
        addImageOrderDtoList(imageOrderDtoList, addImageIdList.get(1), 3);
        addImageOrderDtoList(imageOrderDtoList, addImageIdList.get(2), 4);


        String name = "수정한 이름";
        String description = "수정한 설명";
        int originalPrice = 20000;
        String contents = "수정된 내용";
        ItemStatus status = ItemStatus.HIDDEN;
        boolean deliveryFree = false;
        boolean inStock = false;
        int remaining = 0;
        DiscountType discountType = DiscountType.FLAT_RATE;
        ItemType itemType = ItemType.TOPPING;
        int discountDegree = 1000;
        int salePrice = 19000;
        ItemUpdateDto requestDto = ItemUpdateDto.builder()
                .itemType(itemType)
                .name(name)
                .description(description)
                .originalPrice(originalPrice)
                .discountType(discountType)
                .discountDegree(discountDegree)
                .salePrice(salePrice)
                .inStock(inStock)
                .remaining(remaining)
                .contents(contents)
                .itemIcons("")
                .deliveryFree(deliveryFree)
                .itemStatus(status)
                .addContentImageIdList(addContentImageIdList)
                .deleteContentImageIdList(deleteContentImageIdList)
                .deleteOptionIdList(deleteOptionIdList)
                .itemOptionSaveDtoList(itemOptionSaveDtoList)
                .itemOptionUpdateDtoList(itemOptionUpdateDtoList)
                .deleteImageIdList(deleteImageIdList)
                .addImageIdList(addImageIdList)
                .imageOrderDtoList(imageOrderDtoList)
                .build();


        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/admin/items/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isNotFound());

    }



    @Test
    @DisplayName("정상적으로 상품 삭제하는 테스트")
    public void deleteItem() throws Exception {
       //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        IntStream.range(1,5).forEach(i -> {
            generateOption(item, i);
            generateItemImage(item, i);
            generateItemContentImage(item, i);
        });

       //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/items/{id}", item.getId())
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin_delete_item",
                        links(
                                linkWithRel("self").description("self 링크"),
                                linkWithRel("query_items").description("상품 리스트 조회 링크"),
                                linkWithRel("profile").description("해당 API 관련 문서 링크")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("jwt 토큰")
                        ),
                        pathParameters(
                                parameterWithName("id").description("삭제할 상품 인덱스 id")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("_links.self.href").description("self 링크"),
                                fieldWithPath("_links.query_items.href").description("상품 리스트 조회 링크"),
                                fieldWithPath("_links.profile.href").description("해당 API 관련 문서 링크")
                        )
                ));

        em.flush();
        em.clear();

        Item findItem = itemRepository.findById(item.getId()).get();
        assertThat(findItem.isDeleted()).isTrue();

    }

    @Test
    @DisplayName("삭제할 상품이 없을 경우 404")
    public void deleteItem_404() throws Exception {
        //given
        Item item = generateItem(1, DiscountType.FLAT_RATE, 1000);

        IntStream.range(1,5).forEach(i -> {
            generateOption(item, i);
            generateItemImage(item, i);
            generateItemContentImage(item, i);
        });

        //when & then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/admin/items/999999")
                        .header(HttpHeaders.AUTHORIZATION, getAdminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }















    private void addImageOrderDtoList(List<ItemUpdateDto.ImageOrderDto> imageOrderDtoList, long id, int leakOrder) {
        ItemUpdateDto.ImageOrderDto imageOrderDto = ItemUpdateDto.ImageOrderDto.builder()
                .id(id)
                .leakOrder(leakOrder)
                .build();
        imageOrderDtoList.add(imageOrderDto);
    }

    private void addOptionUpdateDtoInList(ItemOption itemOption, List<ItemUpdateDto.ItemOptionUpdateDto> itemOptionUpdateDtoList) {
        ItemUpdateDto.ItemOptionUpdateDto updateOption1 = ItemUpdateDto.ItemOptionUpdateDto.builder()
                .id(itemOption.getId())
                .name("수정된 이름 옵션")
                .price(3000)
                .remaining(111)
                .build();
        itemOptionUpdateDtoList.add(updateOption1);
    }

    private ItemUpdateDto.ItemOptionSaveDto generateOption(int i) {
        return ItemUpdateDto.ItemOptionSaveDto.builder()
                .name("옵션" + i)
                .price(2000)
                .remaining(99999)
                .build();
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

    private Item generateItem(int i, DiscountType discountType, int discountDegree) {
        Item item = Item.builder()
                .itemType(ItemType.RAW)
                .name("상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(discountType)
                .discountDegree(discountDegree)
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

    private Item generateItemGoods(int i) {
        Item item = Item.builder()
                .itemType(ItemType.GOODS)
                .name("상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .deliveryFree(true)
                .status(ItemStatus.LEAKED)
                .build();
        return itemRepository.save(item);
    }

    private Item generateItemTopping(int i, ItemStatus status) {
        Item item = Item.builder()
                .itemType(ItemType.TOPPING)
                .name("상품" + i)
                .description("상품설명" + i)
                .originalPrice(10000)
                .discountType(DiscountType.FLAT_RATE)
                .discountDegree(1000)
                .salePrice(9000)
                .inStock(true)
                .remaining(999)
                .contents("상세 내용" + i)
                .deliveryFree(true)
                .status(status)
                .build();
        return itemRepository.save(item);
    }


    private ItemImage generateItemImage(int i) {
        ItemImage itemImage = ItemImage.builder()
                .folder("folder" + i)
                .filename("filename" + i +".jpg")
                .build();
        return itemImageRepository.save(itemImage);
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

    private ItemContentImage generateItemContentImage(int i) {
        ItemContentImage itemContentImage = ItemContentImage.builder()
                .folder("folder" + i)
                .filename("filename" + i + ".jpg")
                .build();
        return itemContentImageRepository.save(itemContentImage);
    }

    private ItemContentImage generateItemContentImage(Item item, int i) {
        ItemContentImage itemContentImage = ItemContentImage.builder()
                .item(item)
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