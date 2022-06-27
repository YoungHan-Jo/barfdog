package com.bi.barfdog.service;

import com.bi.barfdog.api.InfoController;
import com.bi.barfdog.api.blogDto.UploadedImageDto;
import com.bi.barfdog.api.itemDto.*;
import com.bi.barfdog.domain.banner.ImgFilenamePath;
import com.bi.barfdog.domain.item.*;
import com.bi.barfdog.repository.item.ItemContentImageRepository;
import com.bi.barfdog.repository.item.ItemImageRepository;
import com.bi.barfdog.repository.item.ItemOptionRepository;
import com.bi.barfdog.repository.item.ItemRepository;
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
    public UploadedImageDto uploadItemImageFile(MultipartFile file) {
        ImgFilenamePath path = storageService.storeItemImg(file);
        UploadedImageDto itemImageDto = saveItemImageAndGetItemImageDto(path);

        return itemImageDto;
    }

    @Transactional
    public UploadedImageDto uploadItemContentImageFile(MultipartFile file) {
        ImgFilenamePath path = storageService.storeItemImg(file);
        UploadedImageDto itemContentImageDto = saveItemContentImageAndGetItemContentImageDto(path);

        return itemContentImageDto;
    }

    @Transactional
    public void createItem(ItemSaveDto requestDto) {
        Item item = saveItem(requestDto);
        saveItemOptions(requestDto, item);
        setItemToItemImage(requestDto, item);
        setItemToItemContentImage(requestDto, item);
    }

    public QueryItemAdminDto queryItem(Long id) {

        QueryItemAdminDto.ItemAdminDto itemAdminDto = itemRepository.findAdminDtoById(id);
        List<QueryItemAdminDto.ItemOptionAdminDto> itemOptionAdminDtoList = itemOptionRepository.findAdminDtoListByItemId(id);
        List<QueryItemAdminDto.ItemImageAdminDto> itemImageAdminDtoList = itemImageRepository.findAdminDtoByItemId(id);
        List<QueryItemAdminDto.ItemContentImageDto> itemContentImageDtoList = itemContentImageRepository.findAdminDtoByItemId(id);

        return QueryItemAdminDto.builder()
                .itemAdminDto(itemAdminDto)
                .itemOptionAdminDtoList(itemOptionAdminDtoList)
                .itemImageAdminDtoList(itemImageAdminDtoList)
                .itemContentImageDtoList(itemContentImageDtoList)
                .build();
    }

    @Transactional
    public void updateItem(Long id, ItemUpdateDto requestDto) {
        Item item = itemRepository.findById(id).get();
        item.update(requestDto);

        itemContentImageRepository.deleteAllById(requestDto.getDeleteContentImageIdList());
        addContentImage(requestDto.getAddContentImageIdList(), item);

        itemOptionRepository.deleteAllById(requestDto.getDeleteOptionIdList());
        saveItemOptions(requestDto, item);
        updateItemOptions(requestDto);

        itemImageRepository.deleteAllById(requestDto.getDeleteImageIdList());
        addItemImages(requestDto, item);
        setItemImagesOrder(requestDto);
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id).get();
        itemOptionRepository.deleteByItem(item);
        itemImageRepository.deleteByItem(item);
        itemContentImageRepository.deleteByItem(item);
        itemRepository.delete(item);
    }














    private void setItemImagesOrder(ItemUpdateDto requestDto) {
        List<ItemUpdateDto.ImageOrderDto> imageOrderDtoList = requestDto.getImageOrderDtoList();
        for (ItemUpdateDto.ImageOrderDto dto : imageOrderDtoList) {
            ItemImage itemImage = itemImageRepository.findById(dto.getId()).get();
            itemImage.setOrder(dto.getLeakOrder());
        }
    }

    private void addItemImages(ItemUpdateDto requestDto, Item item) {
        List<Long> addImageIdList = requestDto.getAddImageIdList();
        for (Long imageId : addImageIdList) {
            ItemImage itemImage = itemImageRepository.findById(imageId).get();
            itemImage.setItem(item);
        }
    }

    private void updateItemOptions(ItemUpdateDto requestDto) {
        List<ItemUpdateDto.ItemOptionUpdateDto> itemOptionUpdateDtoList = requestDto.getItemOptionUpdateDtoList();
        for (ItemUpdateDto.ItemOptionUpdateDto dto : itemOptionUpdateDtoList) {
            ItemOption itemOption = itemOptionRepository.findById(dto.getId()).get();
            itemOption.update(dto);
        }
    }

    private void saveItemOptions(ItemUpdateDto requestDto, Item item) {
        List<ItemUpdateDto.ItemOptionSaveDto> itemOptionSaveDtoList = requestDto.getItemOptionSaveDtoList();
        for (ItemUpdateDto.ItemOptionSaveDto dto : itemOptionSaveDtoList) {
            ItemOption itemOption = ItemOption.builder()
                    .item(item)
                    .name(dto.getName())
                    .optionPrice(dto.getPrice())
                    .remaining(dto.getRemaining())
                    .build();
            itemOptionRepository.save(itemOption);
        }
    }

    private void addContentImage(List<Long> addContentImageIdList, Item item) {
        for (Long contentImageId : addContentImageIdList) {
            ItemContentImage itemContentImage = itemContentImageRepository.findById(contentImageId).get();
            itemContentImage.setItem(item);
        }
    }


    private void setItemToItemContentImage(ItemSaveDto requestDto, Item item) {
        addContentImage(requestDto.getContentImageIdList(), item);
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


    private UploadedImageDto saveItemImageAndGetItemImageDto(ImgFilenamePath path) {
        String filename = path.getFilename();

        ItemImage itemImage = ItemImage.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        ItemImage savedItemImage = itemImageRepository.save(itemImage);

        String url = linkTo(InfoController.class).slash("display").slash("items?filename=" + filename).toString();

        UploadedImageDto itemImageDto = UploadedImageDto.builder()
                .id(savedItemImage.getId())
                .url(url)
                .build();
        return itemImageDto;
    }

    private UploadedImageDto saveItemContentImageAndGetItemContentImageDto(ImgFilenamePath path) {
        String filename = path.getFilename();

        ItemContentImage itemContentImage = ItemContentImage.builder()
                .folder(path.getFolder())
                .filename(filename)
                .build();

        ItemContentImage savedImage = itemContentImageRepository.save(itemContentImage);

        String url = linkTo(InfoController.class).slash("display").slash("items?filename=" + filename).toString();

        UploadedImageDto itemContentImageDto = UploadedImageDto.builder()
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
                .itemIcons(requestDto.getItemIcons())
                .deliveryFree(requestDto.isDeliveryFree())
                .status(requestDto.getItemStatus())
                .build();

        Item savedItem = itemRepository.save(item);
        return savedItem;
    }

}
