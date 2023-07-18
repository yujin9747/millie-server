package com.example.millieserver;

import com.example.millieserver.jwt.JwtAuthFilter;
import com.example.millieserver.jwt.JwtExceptionFilter;
import com.example.millieserver.oauth.CustomOAuth2UserService;
import com.example.millieserver.oauth.MyAuthenticationFailureHandler;
import com.example.millieserver.oauth.MyAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity // Spring Security 활성화, debug 모드 활성화 하여 Security Filter 들이 어떤 순서로 실행되는지 확인
@RequiredArgsConstructor
@Profile("local") // Profile이 local인 경우에만 설정이 동작한다.
public class SecurityConfig {
    private final MyAuthenticationSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthFilter jwtAuthFilter;
    private final MyAuthenticationFailureHandler oAuth2LoginFailureHandler;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable) // 활성화 하면 인증이 되지 않을 시 로그인 창으로 이동한다(세션을 사용하지 않는 RESTful API에는 사용하지 않는다.)
                .cors(Customizer.withDefaults()) // CORS 활성화 -> React 프론트와 작업하므로 CORS 문제를 해결하기 위해 활성화
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // STATELESS는 스프링 시큐리티가 세션을 생성하지 않고, 있어도 사용하지 않겠다는 설정 값
                )
                .authorizeHttpRequests((authorize) -> {
                    authorize.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
                    authorize.requestMatchers("/token/**").permitAll(); // 토근 발급을 위한 경로는 모두 허용
                    authorize.requestMatchers("/", "/css/**","/images/**","/js/**","/favicon.ico","/h2-console/**").permitAll();
                    authorize.anyRequest().authenticated(); // 그 외 경로는 모두 인증 필요
                })
                .oauth2Login((oauth2Login) -> {
                    oauth2Login
                            .successHandler(oAuth2LoginSuccessHandler) // OAuth2 로그인 성공시 처리할 핸들러를 지정해준다.
                            .failureHandler(oAuth2LoginFailureHandler) // OAuth2 로그인 실패시 처리할 핸들러를 지정해준다.
                            .userInfoEndpoint((userInfo) -> {
                                userInfo.userService(customOAuth2UserService); // OAuth2 로그인시 사용자 정보를 가져오는 엔드포인트와 사용자 서비스를 설정
                            });
                });


        /*
        * JwtAuthFilter가 먼저 실행되어야 하는 이유는 UsernamePasswordAuthenticationFilter는 인증되지 않은 사용자의 경우 로그인 페이지로 리디렉션 시키기 때문에,
        * JWT 토큰을 검증하고 토큰의 정보를 이용해서 인증 객체를 SecurityContext에 넣어주어야 인증되었다고 보고 로그인 페이지로 리디렉션 되지 않기 때문입니다.
        * */
        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가한다.
        return http
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class)
                .build();
    }

}
