package Bubble.bubblog.global.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

// 스웨거 설정
@OpenAPIDefinition(
        info = @Info(
                title = "Bubblog API 명세서",
                description = "Bubblog 프로젝트의 REST API 명세서입니다.",
                version = "v1",
                contact = @Contact(
                        name = "문덕영",
                        email = "example@example.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "개발 서버"),
                @Server(url = "https://notYet.com", description = "운영 서버")
        }
)
// jwt 설정
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)

@Configuration
public class SwaggerConfig {
}