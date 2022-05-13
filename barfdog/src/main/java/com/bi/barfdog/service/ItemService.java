package com.bi.barfdog.service;

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

import java.util.ArrayList;
import java.util.List;

import static com.bi.barfdog.api.itemDto.ItemSaveDto.*;

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
    public void createItem(ItemSaveDto requestDto, List<MultipartFile> imgFiles, List<MultipartFile> contentImgFiles) {

        Item savedItem = saveItem(requestDto);

        saveItemOptions(requestDto, savedItem);

        saveItemImagesAndFiles(imgFiles, savedItem);

        saveContentImagesAndFiles(contentImgFiles, savedItem);

    }

    private void saveContentImagesAndFiles(List<MultipartFile> contentImgFiles, Item savedItem) {
        for (int i = 0; i < contentImgFiles.size(); i++) {
            MultipartFile file = contentImgFiles.get(i);
            ImgFilenamePath path = storageService.storeItemContentImg(file);

            ItemContentImage itemContentImage = ItemContentImage.builder()
                    .item(savedItem)
                    .leakedOrder(i + 1)
                    .folder(path.getFolder())
                    .filename(path.getFilename())
                    .build();

            itemContentImageRepository.save(itemContentImage);
        }
    }

    private void saveItemImagesAndFiles(List<MultipartFile> imgFiles, Item savedItem) {
        for (int i = 0; i < imgFiles.size(); i++) {
            MultipartFile file = imgFiles.get(i);
            ImgFilenamePath path = storageService.storeItemImg(file);

            ItemImage itemImage = ItemImage.builder()
                    .item(savedItem)
                    .leakedOrder(i + 1)
                    .folder(path.getFolder())
                    .filename(path.getFilename())
                    .build();
            itemImageRepository.save(itemImage);
        }
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
                .deliveryFree(requestDto.isDeliveryFree())
                .status(requestDto.getItemStatus())
                .build();

        Item savedItem = itemRepository.save(item);
        return savedItem;
    }
}
