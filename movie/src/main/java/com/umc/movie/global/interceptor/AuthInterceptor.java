package com.umc.movie.global.interceptor;

import com.umc.movie.global.error.ApiException;
import com.umc.movie.global.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String MEMBER_ID = "memberId";

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader(
                HttpHeaders.AUTHORIZATION
        );

        if (authorization == null
                || !authorization.startsWith("Bearer ")
                || authorization.substring(7).isBlank()) {

            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "AUTH401_2",
                    "로그인이 필요한 요청입니다."
            );
        }

        String token = authorization.substring(7);
        Long memberId = jwtUtil.getMemberId(token);

        request.setAttribute(MEMBER_ID, memberId);

        return true;
    }
}
