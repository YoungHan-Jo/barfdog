package com.bi.barfdog.scheduler;

import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.member.MemberRepository;
import com.bi.barfdog.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BatchScheduler {

    private final MemberService memberService;

    @Scheduled(cron = "0/10 * * * * *")
    public void testSchedule() {
        System.out.println("배치 작업");
    }

    @Scheduled(cron = "0 0 3 1 * *")
    public void gradeCheck() {
        memberService.gradeScheduler();

    }


}
