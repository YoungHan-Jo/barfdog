package com.bi.barfdog.validator;

import com.bi.barfdog.api.memberDto.MemberSaveRequestDto;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class MemberValidator {

    private final MemberRepository memberRepository;

    public void validate(MemberSaveRequestDto requestDto, Errors errors) {
        if (!requestDto.getPassword().equals(requestDto.getConfirmPassword())) {
            errors.reject("Passwords are different each other","비밀번호와 비밀번호확인이 서로 다릅니다.");
        }
    }

    public void duplicateValidate(MemberSaveRequestDto requestDto, Errors errors) {
        Optional<Member> optionalMember = memberRepository.findByEmail(requestDto.getEmail());
        if (optionalMember.isPresent()) {
            errors.reject("Email is Duplicated","이미 가입된 Email 입니다.");
        }

        optionalMember = memberRepository.findByPhoneNumber(requestDto.getPhoneNumber());
        if (optionalMember.isPresent()) {
            errors.reject("Phone number is Duplicated","이미 가입된 phone number 입니다.");
        }
    }

    public void duplicatePhoneNumberValidate(String phoneNumber, Errors errors) {
        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(phoneNumber);
        if (optionalMember.isPresent()) {
            errors.reject("Phone number is Duplicated","이미 가입된 phone number 입니다.");
        }
    }
}
