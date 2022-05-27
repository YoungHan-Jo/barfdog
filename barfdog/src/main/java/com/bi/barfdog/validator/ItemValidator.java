package com.bi.barfdog.validator;

import com.bi.barfdog.api.itemDto.ItemSaveDto;
import com.bi.barfdog.domain.item.ItemContentImage;
import com.bi.barfdog.domain.item.ItemImage;
import com.bi.barfdog.repository.ItemContentImageRepository;
import com.bi.barfdog.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ItemValidator {

    private final ItemImageRepository itemImageRepository;

    private final ItemContentImageRepository itemContentImageRepository;

    public void validateImage(ItemSaveDto requestDto, Errors errors) {
        List<Long> contentImageIdList = requestDto.getContentImageIdList();
        for (Long id : contentImageIdList) {
            Optional<ItemContentImage> optionalItemContentImage = itemContentImageRepository.findById(id);
            if (!optionalItemContentImage.isPresent()) {
                errors.reject("itemContentImage doesn't exist","존재하지 않는 상품 내용 이미지 id 입니다.");

            }
        }

        List<ItemSaveDto.ItemImageOrderDto> itemImageOrderDtoList = requestDto.getItemImageOrderDtoList();
        for (ItemSaveDto.ItemImageOrderDto itemImageOrderDto : itemImageOrderDtoList) {
            Optional<ItemImage> optionalItemImage = itemImageRepository.findById(itemImageOrderDto.getId());
            if (!optionalItemImage.isPresent()) {
                errors.reject("itemImage doesn't exist","존재하지 않는 상품 이미지 id 입니다.");
            }
        }
    }
}
