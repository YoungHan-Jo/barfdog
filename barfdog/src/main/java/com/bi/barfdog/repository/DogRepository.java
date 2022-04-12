package com.bi.barfdog.repository;

import com.bi.barfdog.domain.dog.Dog;
import com.bi.barfdog.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {


    List<Dog> findByMember(Member member);
}
