package com.example.millieserver.jwt;

import lombok.Getter;

@Getter
public class GeneratedToken {
    String accessToken;

    public GeneratedToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
