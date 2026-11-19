package com.umc.movie.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.movie.dto.ApiResponse;
import com.umc.movie.exception.ApiException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String MEMBER_ID = "memberId";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")
                || authorization.substring(7).isBlank()) {
            writeError(response, HttpStatus.UNAUTHORIZED,
                    "AUTH401_2", "로그인이 필요한 요청입니다.");
            return false;
        }

        try {
            Long memberId = jwtUtil.getMemberId(authorization.substring(7));
            request.setAttribute(MEMBER_ID, memberId);
            return true;

        } catch (ExpiredJwtException e) {
            writeError(response, HttpStatus.UNAUTHORIZED,
                    "AUTH401_4", "토큰이 만료되었습니다. 다시 로그인해주세요.");
            return false;

        } catch (JwtException | IllegalArgumentException e) {
            writeError(response, HttpStatus.UNAUTHORIZED,
                    "AUTH401_3", "유효하지 않은 토큰입니다.");
            return false;
        }
    }

    private void writeError(
            HttpServletResponse response,
            HttpStatus status,
            String code,
            String message
    ) throws Exception {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(
                response.getWriter(),
                ApiResponse.error(code, message)
        );
    }
}