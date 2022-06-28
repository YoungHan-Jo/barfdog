package com.bi.barfdog.validator;

import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.dog.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@RequiredArgsConstructor
@Component
public class DogValidator {

    private final DogRepository dogRepository;

    public void validateMyDog(Member member, Long id, Errors errors) {
        Dog dog = dogRepository.findById(id).get();
        if (dog.getMember() != member) {
            errors.reject("this dog is not mine","내 강아지가 아닙니다.");
        }
    }
}
