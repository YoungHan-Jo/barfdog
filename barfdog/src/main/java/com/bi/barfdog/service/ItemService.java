package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.UploadedImageAdminDto;
import com.bi.barfdog.api.itemDto.ItemSaveDto;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.repository.ItemContentImageRepository;
import com.bi.barfdog.repository.ItemImageRepository;
import com.bi.barfdog.repository.ItemOptionRepository;
import com.bi.barfdog.repository.ItemRepository;
import com.bi.barfdog.service.file.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.bi.barfdog.api.itemDto.ItemSaveDto.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final ItemImageRepository itemImageRepository;
    private final ItemContentImageRepository itemContentImageRepository;

    private final StorageService storageService;

    @Transactional
    public UploadedImageAdminDto uploadItemImageFile(MultipartFile file) {
        ImgFilenamePath path = storageService.storeItemImg(file);
        UploadedImageAdminDto itemImageDto = saveItemImageAndGetItemImageDto(path);

        return itemImageDto;
    }

    @Transactional
    public UploadedImageAdminDto uploadItemContentImageFile(MultipartFile file) {
        ImgFilenamePath path = storageService.storeItemImg(file);
        UploadedImageAdminDto itemContentImageDto = saveItemContentImageAndGetItemContentImageDto(path);

        return itemContentImageDto;
    }

    @Transactional
    public void createItem(ItemSaveDto requestDto) {
        Item item = saveItem(requestDto);
        saveItemOptions(requestDto, item);
        setItemToItemImage(requestDto, item);
        setItemToItemContentImage(requestDto, item);
    }
















    private void setItemToItemContentImage(ItemSaveDto requestDto, Item item) {
        List<Long> contentImageIdList = requestDto.getContentImageIdList();
        for (Long id : contentImageIdList) {
            ItemContentImage itemContentImage = itemContentImageRepository.findById(id).get();
            itemContentImage.setItem(item);
        }
    }

    private void setItemToItemImage(ItemSaveDto requestDto, Item item) {
        List<ItemImageOrderDto> itemImageOrderDtoList = requestDto.getItemImageOrderDtoList();
        for (ItemImageOrderDto itemImageOrderDto : itemImageOrderDtoList) {
            Long id = itemImageOrderDto.getId();
            ItemImage itemImage = itemImageRepository.findById(id).get();
            itemImage.setItem(item);
            itemImage.setOrder(itemImageOrderDto.getLeakOrder());
        }
    }


    private UploadedImageAdminDto saveItemImageAndGetItemImageDto(ImgFilenamePath path) {
        String filename = path.getFilename();

        ItemImage itemImage = ItemImage.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        ItemImage savedItemImage = itemImageRepository.save(itemImage);

        String url = linkTo(InfoController.class).slash("display").slash("items?filename=" + filename).toString();

        UploadedImageAdminDto itemImageDto = UploadedImageAdminDto.builder()
                .id(savedItemImage.getId())
                .url(url)
                .build();
        return itemImageDto;
    }

    private UploadedImageAdminDto saveItemContentImageAndGetItemContentImageDto(ImgFilenamePath path) {
        String filename = path.getFilename();

        ItemContentImage itemContentImage = ItemContentImage.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        ItemContentImage savedImage = itemContentImageRepository.save(itemContentImage);

        String url = linkTo(InfoController.class).slash("display").slash("items?filename=" + filename).toString();

        UploadedImageAdminDto itemContentImageDto = UploadedImageAdminDto.builder()
                .id(savedImage.getId())
                .url(url)
                .build();
        return itemContentImageDto;
    }


    private void saveItemOptions(ItemSaveDto requestDto, Item savedItem) {
        for (ItemOptionSaveDto dto : requestDto.getItemOptionSaveDtoList()) {
            ItemOption itemOption = ItemOption.builder()
                    .item(savedItem)
                    .name(dto.getName())
                    .optionPrice(dto.getPrice())
                    .remaining(dto.getRemaining())
                    .build();
            itemOptionRepository.save(itemOption);
        }
    }

    private Item saveItem(ItemSaveDto requestDto) {
        Item item = Item.builder()
                .itemType(requestDto.getItemType())
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .originalPrice(requestDto.getOriginalPrice())
                .discountType(requestDto.getDiscountType())
                .discountDegree(requestDto.getDiscountDegree())
                .salePrice(requestDto.getSalePrice())
                .inStock(requestDto.isInStock())
                .remaining(requestDto.getRemaining())
                .contents(requestDto.getContents())
                .deliveryFree(requestDto.isDeliveryFree())
                .status(requestDto.getItemStatus())
                .build();

        Item savedItem = itemRepository.save(item);
        return savedItem;
    }


}
