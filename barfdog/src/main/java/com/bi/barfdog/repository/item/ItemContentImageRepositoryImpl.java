package com.bi.barfdog.repository.item;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.item.QItemContentImage.*;

@RequiredArgsConstructor
@Repository
public class ItemContentImageRepositoryImpl implements ItemContentImageRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QueryItemAdminDto.ItemContentImageDto> findAdminDtoByItemId(Long id) {

        List<QueryItemAdminDto.ItemContentImageDto> result = queryFactory
                .select(Projections.constructor(QueryItemAdminDto.ItemContentImageDto.class,
                        itemContentImage.id,
                        itemContentImage.filename,
                        itemContentImage.filename
                ))
                .from(itemContentImage)
                .where(itemContentImage.item.id.eq(id))
                .fetch();
        for (QueryItemAdminDto.ItemContentImageDto itemContentImageDto : result) {
            itemContentImageDto.changeUrl();
        }

        return result;
    }
}
