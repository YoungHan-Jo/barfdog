package com.bi.barfdog.auth;

import com.bi.barfdog.domain.member.Member;
import com.bi.barfdog.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// /login 요청이 오면 동작하지만, formlogin을 disable로 설정해서 동작하지 않음
// 필터를 만들어야함 -> JwtAuthenticationFilter
@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("principalDetailsService의 loadUserByUsername() 실행");

        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if(!optionalMember.isPresent()){
            return null;
        }

        return new PrincipalDetails(optionalMember.get());
    }
}
