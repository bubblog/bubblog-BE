package Bubble.bubblog.global.exception.auth;

import Bubble.bubblog.global.dto.ErrorResponse;
import Bubble.bubblog.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");

        ErrorResponse<Void> errorResponse = ErrorResponse.of(
                ErrorCode.UNAUTHORIZED.getCode(),
                "유효하지 않거나 만료된 토큰입니다."
        );

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
