package com.bi.barfdog.validator;

import com.bi.barfdog.api.memberDto.MemberConditionToPublish;
import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.api.memberDto.MemberUpdateRequestDto;
import com.bi.barfdog.api.memberDto.UpdatePasswordRequestDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Component
public class MemberValidator {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public void validatePasswordConfirm(String password, String confirmPassword, Errors errors) {
        if (!password.equals(confirmPassword)) {
            errors.reject("Passwords are different each other","비밀번호와 비밀번호확인이 서로 다릅니다.");
        }
    }

    public void validatePasswordConfirm(UpdatePasswordRequestDto requestDto, Errors errors) {
        if (!requestDto.getNewPassword().equals(requestDto.getNewPasswordConfirm())) {
            errors.reject("Passwords are different each other","새 비밀번호와 새 비밀번호확인이 서로 다릅니다.");
        }
    }

    public void duplicateValidate(MemberSaveRequestDto requestDto, Errors errors) {
        Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());
        if (optionalMember.isPresent()) {
            errors.reject("Email is Duplicated","이미 가입된 Email 입니다.");
        }

        optionalMember = memberRepository.findByPhoneNumber(requestDto.getPhoneNumber());
        if (optionalMember.isPresent()) {
            errors.reject("Phone number is Duplicated","이미 등록된 휴대전화 입니다.");
        }
    }

    public void duplicateValidate(MemberUpdateRequestDto requestDto, Errors errors) {

        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(requestDto.getPhoneNumber());
        if (optionalMember.isPresent()) {
            errors.reject("Phone number is Duplicated","이미 등록된 휴대전화 입니다.");
        }
    }

    public void duplicatePhoneNumberValidate(String phoneNumber, Errors errors) {
        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(phoneNumber);
        if (optionalMember.isPresent()) {
            errors.reject("Phone number is Duplicated","이미 등록된 휴대전화 입니다.");
        }
    }

    public void validatePassword(Member member, String password, Errors errors) {
        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            errors.reject("Wrong Password","잘못된 비밀번호 입니다.");
        }
    }

    public void validateConditionInPublication(MemberConditionToPublish condition, Errors errors) {
        if (!hasText(condition.getEmail()) && !hasText(condition.getName())) {
            errors.reject("Both conditions can't be empty ","모든 조건이 빈 값일 수 없습니다.");
        }
    }

    public void validateAdmin(Member member, Errors errors) {
        if (!member.getRoleList().contains("ADMIN")) {
            errors.reject("is not admin","관리자 계정이 아닙니다.");
        }
    }

    public void wrongTerm(LocalDate from, LocalDate to, Errors errors) {
        if(from.isAfter(to)) errors.reject("wrong term","잘못된 기간 설정입니다.");
    }

    public void validateMemberIdList(List<Long> memberIdList, Errors errors) {
        for (Long id : memberIdList) {
            Optional<Member> optionalMember = memberRepository.findById(id);
            if (!optionalMember.isPresent()) {
                errors.reject("member id doesn't exist","존재하지 않은 member id 입니다.");
            }
        }
    }

    public void validateHadRecommended(Member member,String recommendCode, Errors errors) {
        if (member.getFirstReward().isRecommend()) {
            errors.reject("already have recommended","이미 추천한 적이 있습니다.");
        }
        if (member.getMyRecommendationCode().equals(recommendCode)) {
            errors.reject("can not recommend yourself","본인은 추천할 수 없습니다.");
        }
    }

    public void isWithdrawalMember(Member member, Errors errors) {
        if (member.isWithdrawal() == true) {
            errors.reject("withdrawn user","탈퇴한 유저입니다.");
        }
    }

    public void validateOrders(Member member, Errors errors) {

    }
}
