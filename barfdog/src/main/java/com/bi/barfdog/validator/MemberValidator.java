package com.bi.barfdog.validator;

import com.bi.barfdog.api.memberDto.MemberConditionPublishCoupon;
import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.api.memberDto.MemberUpdateRequestDto;
import com.bi.barfdog.api.memberDto.UpdatePasswordRequestDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
@Component
public class MemberValidator {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void validatePasswordConfirm(MemberSaveRequestDto requestDto, Errors errors) {
        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
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

    public void validatePassword(Member member, MemberUpdateRequestDto requestDto, Errors errors) {
        if (!bCryptPasswordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            errors.reject("Wrong Password","잘못된 비밀번호 입니다.");
        }
    }

    public void validatePassword(Member member, UpdatePasswordRequestDto requestDto, Errors errors) {
        if (!bCryptPasswordEncoder.matches(requestDto.getPassword(), member.getPassword())) {
            errors.reject("Wrong Password","잘못된 비밀번호 입니다.");
        }
    }

    public void validateConditionInPublication(MemberConditionPublishCoupon condition, Errors errors) {
        if (!hasText(condition.getEmail()) && !hasText(condition.getName())) {
            errors.reject("Both conditions can't be empty ","모든 조건이 빈 값일 수 없습니다.");
        }
    }
}
