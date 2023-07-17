package com.example.millieserver.member;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class MemberSignupRequest {
    @NotNull String email;
    @NotNull String nickname;
}
