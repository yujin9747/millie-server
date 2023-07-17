package com.example.millieserver;

import com.example.millieserver.member.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SecurityUserDto {

    private Long id;

    private String email;
    private String role;
}
