package com.bi.barfdog.repository;

import com.bi.barfdog.api.itemDto.QueryItemAdminDto;
import com.bi.barfdog.domain.item.QItemOption;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.item.QItemOption.*;

@RequiredArgsConstructor
@Repository
public class ItemOptionRepositoryImpl implements ItemOptionRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QueryItemAdminDto.ItemOptionAdminDto> findAdminDtoListByItemId(Long id) {
        return queryFactory
                .select(Projections.constructor(QueryItemAdminDto.ItemOptionAdminDto.class,
                        itemOption.id,
                        itemOption.name,
                        itemOption.optionPrice,
                        itemOption.remaining
                        ))
                .from(itemOption)
                .where(itemOption.item.id.eq(id))
                .fetch();
    }
    
}
