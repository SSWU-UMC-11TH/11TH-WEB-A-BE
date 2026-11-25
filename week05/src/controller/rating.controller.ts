import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  ParseIntPipe,
  Patch,
  Post,
  Req,
  UseGuards,
} from '@nestjs/common';
import {
  ApiTags,
  ApiOperation,
  ApiBearerAuth,
  ApiResponse,
} from '@nestjs/swagger';

import type { Request } from 'express';
import { RatingService } from '../service/rating.service';
import { CreateRatingDto } from '../dto/create-rating.dto';
import { UpdateRatingDto } from '../dto/update-rating.dto';
import { AuthGuard } from '../guard/auth.guard';

interface AuthenticatedRequest extends Request {
  memberId: number;
}

@ApiTags('평점')
@Controller()
export class RatingController {
  constructor(private readonly ratingService: RatingService) {}

  // 회원별 평점 조회 (공개 API)
  @ApiOperation({
    summary: '회원별 평점 조회',
    description:
      '특정 회원이 작성한 평점을 조회합니다. 토큰이 필요하지 않습니다.',
  })
  @ApiResponse({
    status: 200,
    description: '회원별 평점 조회 성공',
  })
  @ApiResponse({
    status: 404,
    description: 'MEMBER404_1 - 해당 회원을 찾을 수 없습니다.',
  })
  @Get('members/:memberId/ratings')
  getMemberRatings(@Param('memberId', ParseIntPipe) memberId: number) {
    return this.ratingService.getRatingsByMember(memberId);
  }

  // 평점 등록
  @ApiOperation({
    summary: '평점 등록',
    description: '로그인한 회원이 영화에 평점을 등록합니다.',
  })
  @ApiBearerAuth('bearerAuth')
  @ApiResponse({
    status: 201,
    description: 'COMMON201 - 평점 등록 성공',
  })
  @ApiResponse({
    status: 401,
    description: 'AUTH401_2 - 로그인이 필요한 요청입니다.',
  })
  @ApiResponse({
    status: 404,
    description: 'MEMBER404_1 - 해당 회원을 찾을 수 없습니다.',
  })
  @ApiResponse({
    status: 409,
    description: 'RATING409_1 - 이미 이 영화에 평점을 남기셨습니다.',
    schema: {
      example: {
        isSuccess: false,
        code: 'RATING409_1',
        message: '이미 이 영화에 평점을 남기셨습니다.',
        result: null,
      },
    },
  })
  @Post('ratings')
  @UseGuards(AuthGuard)
  @HttpCode(HttpStatus.CREATED)
  async create(
    @Req() request: AuthenticatedRequest,
    @Body() dto: CreateRatingDto,
  ) {
    const memberId = request.memberId;
    const result = await this.ratingService.create(memberId, dto);

    return {
      isSuccess: true,
      code: 'COMMON201',
      message: '평점을 등록했습니다.',
      result,
    };
  }

  // 평점 수정
  @ApiOperation({
    summary: '평점 수정',
    description: '로그인한 회원이 본인이 작성한 평점을 수정합니다.',
  })
  @ApiBearerAuth('bearerAuth')
  @ApiResponse({
    status: 200,
    description: 'COMMON200 - 평점 수정 성공',
  })
  @ApiResponse({
    status: 401,
    description: 'AUTH401_2 - 로그인이 필요한 요청입니다.',
  })
  @ApiResponse({
    status: 403,
    description:
      'RATING403_1 - 본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.',
    schema: {
      example: {
        isSuccess: false,
        code: 'RATING403_1',
        message: '본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.',
        result: null,
      },
    },
  })
  @ApiResponse({
    status: 404,
    description: 'RATING404_1 - 해당 평점을 찾을 수 없습니다.',
  })
  @Patch('ratings/:ratingId')
  @UseGuards(AuthGuard)
  @HttpCode(HttpStatus.OK)
  async update(
    @Req() request: AuthenticatedRequest,
    @Param('ratingId', ParseIntPipe) ratingId: number,
    @Body() dto: UpdateRatingDto,
  ) {
    const memberId = request.memberId;
    const result = await this.ratingService.update(memberId, ratingId, dto);

    return {
      isSuccess: true,
      code: 'COMMON200',
      message: '평점을 수정했습니다.',
      result,
    };
  }

  // 평점 삭제
  @ApiOperation({
    summary: '평점 삭제',
    description: '로그인한 회원이 본인이 작성한 평점을 삭제합니다.',
  })
  @ApiBearerAuth('bearerAuth')
  @ApiResponse({
    status: 200,
    description: 'COMMON200 - 평점 삭제 성공',
  })
  @ApiResponse({
    status: 401,
    description: 'AUTH401_2 - 로그인이 필요한 요청입니다.',
  })
  @ApiResponse({
    status: 403,
    description:
      'RATING403_1 - 본인이 작성한 평점만 수정하거나 삭제할 수 있습니다.',
  })
  @ApiResponse({
    status: 404,
    description: 'RATING404_1 - 해당 평점을 찾을 수 없습니다.',
  })
  @Delete('ratings/:ratingId')
  @UseGuards(AuthGuard)
  @HttpCode(HttpStatus.OK)
  async delete(
    @Req() request: AuthenticatedRequest,
    @Param('ratingId', ParseIntPipe) ratingId: number,
  ) {
    const memberId = request.memberId;

    await this.ratingService.delete(memberId, ratingId);

    return {
      isSuccess: true,
      code: 'COMMON200',
      message: '평점을 삭제했습니다.',
      result: null,
    };
  }
}
