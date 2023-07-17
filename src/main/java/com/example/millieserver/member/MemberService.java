package com.example.millieserver.member;

import com.example.millieserver.jwt.GeneratedToken;
import com.example.millieserver.jwt.JwtUtil;
import com.example.millieserver.util.token.Token;
import com.example.millieserver.util.token.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final TokenGenerator tokenGenerator;
    private final JwtUtil jwtUtil;

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public GeneratedToken save(MemberSignupRequest request) {
        Member member = Member.builder()
                        .nickname(request.getNickname())
                        .email(request.getEmail())
                        .build();
        memberRepository.save(member);
        return jwtUtil.generateToken(request.getEmail(), String.valueOf(UserRole.USER));
//        return tokenGenerator.generateToken(member.getId());
    }
}
