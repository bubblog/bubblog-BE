package Bubble.bubblog.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                  // /api/ 로 시작하는 모든 엔드포인트
                .allowedOrigins("http://localhost:3000", "https://bubblog-fe.vercel.app","https://bubblog.kro.kr")// 프론트엔드 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);               // 쿠키(리프레시 토큰) 허용
    }
}