package com.bi.barfdog.repository.item;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.item.QItemImage.*;

@RequiredArgsConstructor
@Repository
public class ItemImageRepositoryImpl implements ItemImageRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QueryItemAdminDto.ItemImageAdminDto> findAdminDtoByItemId(Long id) {
        List<QueryItemAdminDto.ItemImageAdminDto> result = queryFactory
                .select(Projections.constructor(QueryItemAdminDto.ItemImageAdminDto.class,
                        itemImage.id,
                        itemImage.leakOrder,
                        itemImage.filename,
                        itemImage.filename
                ))
                .from(itemImage)
                .where(itemImage.item.id.eq(id))
                .fetch();

        for (QueryItemAdminDto.ItemImageAdminDto itemImageAdminDto : result) {
            itemImageAdminDto.changeUrl();
        }


        return result;
    }

    @Override
    public List<String> findFilename() {
        return queryFactory
                .select(itemImage.filename)
                .from(itemImage)
                .fetch()
                ;
    }
}
