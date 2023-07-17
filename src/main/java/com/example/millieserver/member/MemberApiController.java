package com.example.millieserver.member;

import com.example.millieserver.jwt.GeneratedToken;
import com.example.millieserver.support.ApiResponse;
import com.example.millieserver.support.ApiResponseGenerator;
import com.example.millieserver.support.MessageCode;
import com.example.millieserver.util.token.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000, http://http://inflearn-aws-s3.s3-website-us-east-1.amazonaws.com", allowCredentials = "true")
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/member")
    public ApiResponse<ApiResponse.SuccessBody<GeneratedToken>> registerMember(@RequestBody @Validated MemberSignupRequest request) {
        return ApiResponseGenerator.success(
                memberService.save(request), HttpStatus.OK, MessageCode.SUCCESS);
    }

    @GetMapping("/member")
    public ApiResponse<ApiResponse.SuccessBody<Member>> getMember(@RequestParam String email) {
        return ApiResponseGenerator.success(
                memberService.findByEmail(email).get(), HttpStatus.OK, MessageCode.SUCCESS);
    }
}
