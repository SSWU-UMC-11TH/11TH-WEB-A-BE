package com.umc.movie.domain.service;

import com.umc.movie.domain.dto.request.CreateFavoriteRequest;
import com.umc.movie.domain.dto.response.FavoriteResponse;
import com.umc.movie.domain.entity.Favorite;
import com.umc.movie.domain.entity.Member;
import com.umc.movie.domain.repository.FavoriteRepository;
import com.umc.movie.domain.repository.MemberRepository;
import com.umc.movie.global.error.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private final Member member = Member.builder()
            .email("challenger@umc.com")
            .password("암호화된비밀번호")
            .nickname("영화조아")
            .build();

    @Test
    @DisplayName("아직 즐겨찾기하지 않은 영화면 즐겨찾기를 저장한다")
    void createFavorite() {
        given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        given(favoriteRepository.existsByMember_MemberIdAndMovieId(
                1L, 550L
        )).willReturn(false);

        given(favoriteRepository.save(any(Favorite.class)))
                .willReturn(Favorite.builder()
                        .member(member)
                        .movieId(550L)
                        .build());

        FavoriteResponse response = favoriteService.create(
                1L, new CreateFavoriteRequest(550L)
        );

        assertThat(response.movieId()).isEqualTo(550L);

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    @DisplayName("이미 즐겨찾기한 영화면 FAVORITE409_1 예외를 던지고 저장하지 않는다")
    void createFavoriteWhenDuplicated() {
        given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));

        given(favoriteRepository.existsByMember_MemberIdAndMovieId(
                1L, 550L
        )).willReturn(true);

        assertThatThrownBy(() -> favoriteService.create(
                1L, new CreateFavoriteRequest(550L)
        ))
                .isInstanceOfSatisfying(ApiException.class, exception -> {
                    assertThat(exception.getStatus())
                            .isEqualTo(HttpStatus.CONFLICT);

                    assertThat(exception.getCode())
                            .isEqualTo("FAVORITE409_1");

                    assertThat(exception.getMessage())
                            .isEqualTo("이미 이 영화를 즐겨찾기하셨습니다.");
                });

        verify(favoriteRepository, never())
                .save(any(Favorite.class));
    }
}
