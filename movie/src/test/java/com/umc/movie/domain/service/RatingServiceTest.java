package com.umc.movie.domain.service;

import com.umc.movie.domain.dto.request.CreateRatingRequest;
import com.umc.movie.domain.dto.request.UpdateRatingRequest;
import com.umc.movie.domain.entity.Member;
import com.umc.movie.domain.entity.Rating;
import com.umc.movie.domain.repository.MemberRepository;
import com.umc.movie.domain.repository.RatingRepository;
import com.umc.movie.global.error.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RatingService ratingService;

    private final Member member = Member.builder()
            .email("challenger@umc.com")
            .password("암호화된비밀번호")
            .nickname("영화조아")
            .build();

    @Test
    @DisplayName("같은 영화에 평점을 중복 등록하면 RATING409_1 예외를 던진다")
    void createRatingWhenDuplicated() {

        given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        given(ratingRepository.existsByMember_MemberIdAndMovieId(
                1L, 550L
        )).willReturn(true);

        CreateRatingRequest request =
                new CreateRatingRequest(550L, 5, "재미있는 영화");

        assertThatThrownBy(() -> ratingService.create(1L, request))
                .isInstanceOfSatisfying(ApiException.class, exception -> {

                    assertThat(exception.getStatus())
                            .isEqualTo(HttpStatus.CONFLICT);

                    assertThat(exception.getCode())
                            .isEqualTo("RATING409_1");

                    assertThat(exception.getMessage())
                            .isEqualTo("이미 이 영화에 평점을 남기셨습니다.");
                });

        verify(ratingRepository, never())
                .save(any(Rating.class));
    }

    @Test
    @DisplayName("다른 회원의 평점을 수정하면 RATING403_1 예외를 던진다")
    void updateRatingWhenNotOwner() {

        ReflectionTestUtils.setField(member, "memberId", 1L);

        Rating rating = Rating.builder()
                .member(member)
                .movieId(550L)
                .score(5)
                .comment("기존 후기")
                .build();

        given(ratingRepository.findById(10L))
                .willReturn(Optional.of(rating));

        UpdateRatingRequest request =
                new UpdateRatingRequest(3, "수정된 후기");

        assertThatThrownBy(() ->
                ratingService.update(10L, 2L, request)
        ).isInstanceOfSatisfying(ApiException.class, exception -> {

            assertThat(exception.getStatus())
                    .isEqualTo(HttpStatus.FORBIDDEN);

            assertThat(exception.getCode())
                    .isEqualTo("RATING403_1");

            assertThat(exception.getMessage())
                    .isEqualTo(
                            "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다."
                    );
        });

        assertThat(rating.getScore()).isEqualTo(5);
        assertThat(rating.getComment()).isEqualTo("기존 후기");
    }
}