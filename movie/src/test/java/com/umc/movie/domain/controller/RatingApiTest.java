package com.umc.movie.domain.controller;

import com.umc.movie.domain.dto.request.CreateRatingRequest;
import com.umc.movie.domain.entity.Member;
import com.umc.movie.domain.repository.MemberRepository;
import com.umc.movie.domain.repository.RatingRepository;
import com.umc.movie.global.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class RatingApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Member member;
    private String authorizationHeader;

    @BeforeEach
    void setUp() {

        member = memberRepository.save(
                Member.builder()
                        .email("challenger@umc.com")
                        .password("$2a$10$abcdefghijklmnopqrstuv")
                        .nickname("영화조아")
                        .build()
        );

        authorizationHeader = "Bearer " +
                jwtUtil.createAccessToken(
                        member.getMemberId(),
                        member.getEmail()
                );
    }

    @Test
    @DisplayName("토큰 없이 평점을 등록하면 401과 AUTH401_2를 응답한다")
    void createRatingWithoutToken() throws Exception {

        CreateRatingRequest request =
                new CreateRatingRequest(550L, 5, "재미있는 영화");

        mockMvc.perform(post("/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("AUTH401_2"));

        assertThat(
                ratingRepository.existsByMember_MemberIdAndMovieId(
                        member.getMemberId(), 550L
                )
        ).isFalse();
    }

    @Test
    @DisplayName("토큰을 담아 평점을 등록하면 201과 평점 ID를 응답한다")
    void createRating() throws Exception {

        CreateRatingRequest request =
                new CreateRatingRequest(550L, 5, "재미있는 영화");
        
        mockMvc.perform(post("/ratings")
                        .header(HttpHeaders.AUTHORIZATION,
                                authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON201"))
                .andExpect(jsonPath("$.result").isNumber());

        assertThat(
                ratingRepository.existsByMember_MemberIdAndMovieId(
                        member.getMemberId(), 550L
                )
        ).isTrue();
    }
}
