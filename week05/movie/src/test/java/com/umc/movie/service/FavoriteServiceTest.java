package com.umc.movie.service;

import com.umc.movie.dto.CreateFavoriteRequest;
import com.umc.movie.dto.FavoriteResponse;
import com.umc.movie.entity.Favorite;
import com.umc.movie.entity.Member;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.FavoriteRepository;
import com.umc.movie.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    @DisplayName("즐겨찾기를 정상적으로 등록한다")
    void createFavoriteSuccess() {
        // given
        Member member = Member.builder()
                .email("test@umc.com")
                .password("encoded-password")
                .nickname("테스트회원")
                .build();

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        when(favoriteRepository.existsByMember_IdAndMovieId(1L, 550L))
                .thenReturn(false);

        when(favoriteRepository.save(any(Favorite.class)))
                .thenReturn(Favorite.builder()
                        .member(member)
                        .movieId(550L)
                        .build());

        // when
        FavoriteResponse response = favoriteService.create(
                1L, new CreateFavoriteRequest(550L)
        );

        // then
        assertThat(response.movieId()).isEqualTo(550L);
        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("이미 즐겨찾기한 영화는 중복 등록할 수 없다")
    void createFavoriteDuplicate() {
        // given
        Member member = Member.builder()
                .email("test@umc.com")
                .password("encoded-password")
                .nickname("테스트회원")
                .build();

        when(memberRepository.findById(1L))
                .thenReturn(Optional.of(member));

        when(favoriteRepository.existsByMember_IdAndMovieId(1L, 550L))
                .thenReturn(true);

        // when & then
        ApiException exception = catchThrowableOfType(
                () -> favoriteService.create(
                        1L, new CreateFavoriteRequest(550L)),
                ApiException.class
        );

        assertThat(exception.getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        assertThat(exception.getCode())
                .isEqualTo("FAVORITE409_1");

        assertThat(exception.getMessage())
                .isEqualTo("이미 이 영화를 즐겨찾기하셨습니다.");

        verify(favoriteRepository, never())
                .save(any(Favorite.class));
    }
}