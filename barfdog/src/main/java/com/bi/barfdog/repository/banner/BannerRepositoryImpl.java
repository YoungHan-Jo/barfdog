package com.bi.barfdog.repository.banner;

import com.bi.barfdog.api.bannerDto.*;
import com.bi.barfdog.api.barfDto.HomePageDto;
import com.bi.barfdog.domain.banner.*;
import com.bi.barfdog.domain.member.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.bi.barfdog.domain.banner.QMainBanner.mainBanner;
import static com.bi.barfdog.domain.banner.QMyPageBanner.myPageBanner;
import static com.bi.barfdog.domain.banner.QPopupBanner.popupBanner;
import static com.bi.barfdog.domain.banner.QTopBanner.topBanner;

@Repository
@RequiredArgsConstructor
public class BannerRepositoryImpl implements BannerRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<MainBanner> findMainBanners() {
        return queryFactory
                .selectFrom(mainBanner)
                .fetch();
    }

    @Override
    public List<PopupBanner> findPopupBanners() {

        return queryFactory
                .selectFrom(popupBanner)
                .fetch();
    }

    @Override
    public List<TopBanner> findTopBanners() {
        return queryFactory
                .selectFrom(topBanner)
                .fetch();

    }

    @Override
    public MainBanner findMainBannerByOrder(int order) {
        return queryFactory
                .selectFrom(mainBanner)
                .where(mainBanner.leakedOrder.eq(order))
                .fetchOne();
    }

    @Override
    public PopupBanner findPopupBannerByOrder(int order) {

        return queryFactory
                .selectFrom(popupBanner)
                .where(popupBanner.leakedOrder.eq(order))
                .fetchOne();
    }

    @Override
    public List<MainBannerListResponseDto> findMainBannersDtos() {
        return queryFactory
                .select(Projections.constructor(MainBannerListResponseDto.class,
                        mainBanner.id,
                        mainBanner.leakedOrder,
                        mainBanner.name,
                        mainBanner.targets,
                        mainBanner.createdDate,
                        mainBanner.modifiedDate,
                        mainBanner.imgFile.filenamePc,
                        mainBanner.imgFile.filenameMobile))
                .from(mainBanner)
                .fetch();
    }

    @Override
    public Optional<MainBannerResponseDto> findMainBannerDtoById(Long id) {
        MainBannerResponseDto result = queryFactory
                .select(Projections.constructor(MainBannerResponseDto.class,
                        mainBanner.id,
                        mainBanner.name,
                        mainBanner.targets,
                        mainBanner.status,
                        mainBanner.imgFile.filenamePc,
                        mainBanner.imgFile.filenameMobile,
                        mainBanner.pcLinkUrl,
                        mainBanner.mobileLinkUrl
                        ))
                .from(mainBanner)
                .where(mainBanner.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<MainBanner> findMainBannerById(Long id) {
        MainBanner result = queryFactory
                .selectFrom(mainBanner)
                .where(mainBanner.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<MyPageBannerResponseDto> findFirstMyPageBanner() {
        MyPageBannerResponseDto result = queryFactory
                .select(Projections.constructor(MyPageBannerResponseDto.class,
                        myPageBanner.id,
                        myPageBanner.name,
                        myPageBanner.status,
                        myPageBanner.imgFile.filenamePc,
                        myPageBanner.imgFile.filenameMobile,
                        myPageBanner.pcLinkUrl,
                        myPageBanner.mobileLinkUrl))
                .from(myPageBanner)
                .orderBy(myPageBanner.id.asc())
                .offset(0)
                .limit(1)
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<TopBannerResponseDto> findFirstTopBannerDto() {
        TopBannerResponseDto result = queryFactory
                .select(Projections.constructor(TopBannerResponseDto.class,
                        topBanner.id,
                        topBanner.name,
                        topBanner.status,
                        topBanner.backgroundColor,
                        topBanner.fontColor,
                        topBanner.pcLinkUrl,
                        topBanner.mobileLinkUrl
                ))
                .from(topBanner)
                .orderBy(topBanner.id.asc())
                .offset(0)
                .limit(1)
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<PopupBannerListResponseDto> findPopupBannerDtos() {
        return queryFactory
                .select(Projections.constructor(PopupBannerListResponseDto.class,
                        popupBanner.id,
                        popupBanner.leakedOrder,
                        popupBanner.position,
                        popupBanner.name,
                        popupBanner.createdDate,
                        popupBanner.modifiedDate,
                        popupBanner.imgFile.filenamePc,
                        popupBanner.imgFile.filenameMobile
                ))
                .from(popupBanner)
                .fetch();
    }

    @Override
    public Optional<PopupBannerResponseDto> findPopupBannerDtoById(Long id) {
        PopupBannerResponseDto result = queryFactory
                .select(Projections.constructor(PopupBannerResponseDto.class,
                        popupBanner.id,
                        popupBanner.name,
                        popupBanner.status,
                        popupBanner.position,
                        popupBanner.imgFile.filenamePc,
                        popupBanner.imgFile.filenameMobile,
                        popupBanner.pcLinkUrl,
                        popupBanner.mobileLinkUrl
                ))
                .from(popupBanner)
                .where(popupBanner.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<PopupBanner> findPopupBannerById(Long id) {
        PopupBanner result = queryFactory
                .selectFrom(popupBanner)
                .where(popupBanner.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<MyPageBanner> findMyPageBannerById(Long id) {
        MyPageBanner result = queryFactory
                .selectFrom(myPageBanner)
                .where(myPageBanner.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<TopBanner> findTopBannerById(Long id) {
        TopBanner result = queryFactory
                .selectFrom(topBanner)
                .where(topBanner.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public List<MainBanner> findMainBannersByName(String name) {
        return queryFactory
                .selectFrom(mainBanner)
                .where(mainBanner.name.eq(name))
                .fetch();
    }

    @Override
    public HomePageDto.TopBannerDto findTopBannerDto() {
        return queryFactory
                .select(Projections.constructor(HomePageDto.TopBannerDto.class,
                        topBanner.name,
                        topBanner.backgroundColor,
                        topBanner.fontColor,
                        topBanner.pcLinkUrl,
                        topBanner.mobileLinkUrl
                ))
                .from(topBanner)
                .fetchOne();
    }

    @Override
    public List<HomePageDto.MainBannerDto> findMainBannerDtoListByMember(Member member) {
        List<BannerTargets> bannerTargetsList = new ArrayList<>();
        bannerTargetsList.add(BannerTargets.ALL);
        if (member != null) {
            List<String> roleList = member.getRoleList();
            for (String str : roleList) {
                BannerTargets bannerTargets = BannerTargets.valueOf(str.toUpperCase());
                bannerTargetsList.add(bannerTargets);
            }
        } else {
            bannerTargetsList.add(BannerTargets.GUEST);
        }

        List<HomePageDto.MainBannerDto> result = queryFactory
                .select(Projections.constructor(HomePageDto.MainBannerDto.class,
                        mainBanner.id,
                        mainBanner.leakedOrder,
                        mainBanner.name,
                        mainBanner.imgFile.filenamePc,
                        mainBanner.imgFile.filenamePc,
                        mainBanner.pcLinkUrl,
                        mainBanner.imgFile.filenameMobile,
                        mainBanner.imgFile.filenameMobile,
                        mainBanner.mobileLinkUrl
                ))
                .from(mainBanner)
                .where(mainBanner.targets.in(bannerTargetsList).and(mainBanner.status.eq(BannerStatus.LEAKED)))
                .orderBy(mainBanner.leakedOrder.asc())
                .fetch();
        for (HomePageDto.MainBannerDto dto : result) {
            dto.changeUrl();
        }

        return result;
    }

    @Override
    public List<MyPageBanner> findMyPageBanners() {

        return queryFactory
                .selectFrom(myPageBanner)
                .fetch();
    }
}
