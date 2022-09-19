package com.bi.barfdog.repository.guest;

import com.bi.barfdog.api.guestDto.QueryAdminGuestDto;
import com.bi.barfdog.api.guestDto.QueryGuestCond;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bi.barfdog.domain.guest.QGuest.*;
import static com.bi.barfdog.domain.member.QMember.*;

@RequiredArgsConstructor
@Repository
public class GuestRepositoryImpl implements GuestRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<QueryAdminGuestDto> findAdminGuestDtos(Pageable pageable, QueryGuestCond cond) {
        List<QueryAdminGuestDto> results = queryFactory
                .select(Projections.constructor(QueryAdminGuestDto.class,
                        guest.name,
                        guest.email,
                        guest.phoneNumber,
                        guest.createdDate,
                        member.id,
                        member.name,
                        member.email,
                        member.phoneNumber,
                        member.createdDate,
                        member.firstPaymentDate,
                        member.isPaid
                ))
                .from(guest)
                .leftJoin(member).on(member.email.eq(guest.email).or(member.phoneNumber.eq(guest.phoneNumber)))
                .where(
                        guestNameContains(cond.getName()),
                        guestEmailContains(cond.getEmail()),
                        guestPhoneNumberContains(cond.getPhoneNumber())
                )
                .orderBy(guest.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(guest.count())
                .from(guest)
                .leftJoin(member).on(member.email.eq(guest.email).or(member.phoneNumber.eq(guest.phoneNumber)))
                .where(
                        guestNameContains(cond.getName()),
                        guestEmailContains(cond.getEmail()),
                        guestPhoneNumberContains(cond.getPhoneNumber())
                )
                .fetchOne();

        return new PageImpl(results, pageable, totalCount);
    }

    private BooleanExpression guestPhoneNumberContains(String phoneNumber) {
        return isNotEmpty(phoneNumber) ? guest.phoneNumber.contains(phoneNumber).or(member.phoneNumber.contains(phoneNumber)) : null;
    }

    private BooleanExpression guestEmailContains(String email) {
        return isNotEmpty(email) ? guest.email.contains(email).or(member.email.contains(email)) : null;
    }

    private BooleanExpression guestNameContains(String name) {
        return isNotEmpty(name) ? guest.name.contains(name).or(member.name.contains(name)) : null;
    }

    private boolean isNotEmpty(String keyword) {
        return keyword != null && keyword.trim().length() > 0;
    }
}
