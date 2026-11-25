import {
  Controller,
  Get,
  Param,
  UseGuards,
  Req,
  Body,
  HttpCode,
  HttpStatus,
  Post,
  Patch,
} from '@nestjs/common';

import {
  ApiTags,
  ApiOperation,
  ApiBearerAuth,
  ApiParam,
  ApiResponse,
} from '@nestjs/swagger';

import type { Request } from 'express';
import { MemberUpdateRequestDto } from '../dto/member-update-request.dto';
import { MemberService } from '../service/member.service';
import { CreateMemberDto } from '../dto/create-member.dto';
import { AuthGuard } from '../guard/auth.guard';

// JWT 인증 후 memberId가 추가된 Request 타입
interface AuthenticatedRequest extends Request {
  memberId: number;
}

@ApiTags('회원')
@Controller('members')
export class MemberController {
  constructor(private readonly memberService: MemberService) {}

  // 닉네임 중복 확인
  @ApiOperation({
    summary: '닉네임 중복 확인',
    description: '사용 가능한 닉네임인지 확인합니다.',
  })
  @ApiParam({
    name: 'nickname',
    description: '중복 확인할 닉네임',
    example: 'UMCine',
  })
  @Get('nickname/:nickname')
  checkNickname(@Param('nickname') nickname: string) {
    return this.memberService.checkNickname(nickname);
  }

  // 이메일 중복 확인
  @ApiOperation({
    summary: '이메일 중복 확인',
    description: '사용 가능한 이메일인지 확인합니다.',
  })
  @ApiParam({
    name: 'email',
    description: '중복 확인할 이메일',
    example: 'user@example.com',
  })
  @Get('email/:email')
  checkEmail(@Param('email') email: string) {
    return this.memberService.checkEmail(email);
  }

  // 회원가입
  @ApiOperation({
    summary: '회원가입',
    description:
      '이메일, 비밀번호, 닉네임으로 회원을 등록합니다. 토큰이 없어도 호출할 수 있는 공개 API입니다.',
  })
  @Post()
  @HttpCode(HttpStatus.CREATED)
  async signUp(@Body() dto: CreateMemberDto) {
    const result = await this.memberService.signUp(dto);

    return {
      isSuccess: true,
      code: 'COMMON201',
      message: '회원가입에 성공했습니다.',
      result,
    };
  }

  // 내 정보 조회 (JWT 인증 필요)
  @ApiOperation({
    summary: '내 정보 조회',
    description: '로그인한 회원의 정보를 조회합니다.',
  })
  @ApiBearerAuth()
  @Get('me')
  @UseGuards(AuthGuard)
  async getMyInfo(@Req() request: AuthenticatedRequest) {
    const memberId = request.memberId;

    const result = await this.memberService.getMyInfo(memberId);

    return {
      isSuccess: true,
      code: 'COMMON200',
      message: '회원 정보를 조회했습니다.',
      result,
    };
  }

  // 내 정보 수정 (JWT 인증 필요)
  @ApiOperation({
    summary: '내 정보 수정',
    description: '로그인한 회원의 정보를 수정합니다.',
  })
  @ApiBearerAuth('bearerAuth')
  @ApiResponse({
    status: 200,
    description: 'COMMON200 - 성공',
  })
  @ApiResponse({
    status: 401,
    description: 'AUTH401_2 - 로그인이 필요한 요청입니다.',
  })
  @Patch('me')
  @UseGuards(AuthGuard)
  async updateMyInfo(
    @Req() request: AuthenticatedRequest,
    @Body() dto: MemberUpdateRequestDto,
  ) {
    const memberId = request.memberId;

    const result = await this.memberService.updateMyInfo(memberId, dto);

    return {
      isSuccess: true,
      code: 'COMMON200',
      message: '회원 정보를 수정했습니다.',
      result,
    };
  }
}
