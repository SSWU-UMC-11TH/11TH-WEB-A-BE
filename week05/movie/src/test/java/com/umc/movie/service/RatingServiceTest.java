package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    @DisplayName("동일 영화에 평점을 중복 등록할 수 없다")
    void createRatingDuplicate() {
        // given
        Member member = Member.builder()
                .email("test@umc.com")
                .password("encoded-password")
                .nickname("테스트회원")
                .build();

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        when(ratingRepository.existsByMemberIdAndMovieId(1L, 550L))
                .thenReturn(true);

        CreateRatingRequest request =
                new CreateRatingRequest(550L, (byte) 5, "좋은 영화");

        // when & then
        ApiException exception = catchThrowableOfType(
                () -> ratingService.createRating(1L, request),
                ApiException.class
        );

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        assertThat(exception.getCode())
                .isEqualTo("RATING409_1");

        verify(ratingRepository, never())
                .saveAndFlush(any(Rating.class));
    }

    @Test
    @DisplayName("다른 회원이 작성한 평점은 수정할 수 없다")
    void updateRatingForbidden() {
        // given
        Member owner = Member.builder()
                .email("owner@umc.com")
                .password("encoded-password")
                .nickname("작성자")
                .build();

        ReflectionTestUtils.setField(owner, "id", 1L);

        Rating rating = Rating.builder()
                .member(owner)
                .movieId(550L)
                .score((byte) 5)
                .comment("기존 코멘트")
                .build();

        when(ratingRepository.findById(10L))
                .thenReturn(Optional.of(rating));

        UpdateRatingRequest request =
                new UpdateRatingRequest((byte) 3, "수정 코멘트");

        // when & then
        ApiException exception = catchThrowableOfType(
                () -> ratingService.updateRating(2L, 10L, request),
                ApiException.class
        );

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.FORBIDDEN);

        assertThat(exception.getCode())
                .isEqualTo("RATING403_1");

        assertThat(rating.getScore()).isEqualTo((byte) 5);
        assertThat(rating.getComment()).isEqualTo("기존 코멘트");
    }
}