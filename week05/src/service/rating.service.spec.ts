import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { ApiException } from '../exception/api.exception';
import { Rating } from '../entity/rating.entity';
import { Member } from '../entity/member.entity';
import { RatingService } from './rating.service';

describe('RatingService', () => {
  let ratingService: RatingService;

  const ratingRepository = {
    findOne: jest.fn(),
    save: jest.fn(),
    create: jest.fn(),
  };

  const memberRepository = {
    findOneBy: jest.fn(),
  };

  const member = {
    memberId: 1,
    nickname: '영화조아',
  } as Member;

  beforeEach(async () => {
    jest.clearAllMocks();

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        RatingService,
        {
          provide: getRepositoryToken(Rating),
          useValue: ratingRepository,
        },
        {
          provide: getRepositoryToken(Member),
          useValue: memberRepository,
        },
      ],
    }).compile();

    ratingService = module.get<RatingService>(RatingService);
  });

  it('같은 영화에 평점을 중복 등록하면 409와 RATING409_1 예외를 던지고 저장하지 않는다', async () => {
    // given
    memberRepository.findOneBy.mockResolvedValue(member);

    ratingRepository.findOne.mockResolvedValue({
      ratingId: 1,
      movieId: 550,
      score: 4,
      member,
    });

    // when
    const error = await ratingService
      .create(1, {
        movieId: 550,
        score: 4,
        comment: '재미있는 영화입니다.',
      })
      .catch((error) => error);

    // then
    expect(error).toBeInstanceOf(ApiException);
    expect(error.getStatus()).toBe(409);
    expect(error.code).toBe('RATING409_1');

    expect(ratingRepository.save).not.toHaveBeenCalled();
  });

  it('다른 회원이 작성한 평점을 수정하려 하면 403과 RATING403_1 예외를 던지고 저장하지 않는다', async () => {
    // given: 회원 번호 2번이 작성한 평점
    ratingRepository.findOne.mockResolvedValue({
      ratingId: 1,
      movieId: 550,
      score: 4,
      member: {
        memberId: 2,
      },
    });

    // when: 회원 번호 1번이 수정을 시도
    const error = await ratingService
      .update(1, 1, {
        score: 5,
        comment: '평점 수정',
      })
      .catch((error) => error);

    // then
    expect(error).toBeInstanceOf(ApiException);
    expect(error.getStatus()).toBe(403);
    expect(error.code).toBe('RATING403_1');

    expect(ratingRepository.save).not.toHaveBeenCalled();
  });
});
