package Bubble.bubblog.global.config;

import Bubble.bubblog.global.exception.auth.CustomAccessDeniedHandler;
import Bubble.bubblog.global.exception.auth.CustomAuthenticationEntryPoint;
import Bubble.bubblog.global.util.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor    // 생성자 자동 주입
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                // Swagger 허용
                                .requestMatchers(
                                        "/api/swagger-ui/**",
                                        "/api/swagger-resources/**",
                                        "/api/v3/api-docs/**",
                                        "/api/v3/api-docs.yaml",
                                        "/swagger-ui.html"
                                ).permitAll()
                                // 로그인, 회원가입, 비밀번호 재설정 같은 엔드포인트 허용
                                .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/reissue").permitAll()
                                // 게시글 조회 관련 API 허용 (특정 게시글의 전체 댓글 조회도 여기에 포함)
                                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                                // 조회수 증가 API 허용
                                .requestMatchers(HttpMethod.PUT, "/api/posts/*/view").permitAll()
                                // 말투 조회 관련 API 허용
                                .requestMatchers(HttpMethod.GET, "/api/personas/**").permitAll()
                                // 사용자 정보 조회 관련 API 허용
                                .requestMatchers(HttpMethod.GET, "/api/users/{userId}").permitAll()
                                // 댓글 조회 관련 API 허용
                                .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
