package com.example.millieserver.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.jwt") // application-app.yml 파일에 있는 spring.jwt 값으로 바인딩
public class JwtProperties {
    private String secret;
}
