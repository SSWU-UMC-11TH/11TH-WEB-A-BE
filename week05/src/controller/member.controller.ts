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

import type { Request } from 'express';
import { MemberUpdateRequestDto } from '../dto/member-update-request.dto';
import { MemberService } from '../service/member.service';
import { CreateMemberDto } from '../dto/create-member.dto';
import { AuthGuard } from '../guard/auth.guard';

// JWT 인증 후 memberId가 추가된 Request 타입
interface AuthenticatedRequest extends Request {
  memberId: number;
}

@Controller('members')
export class MemberController {
  constructor(private readonly memberService: MemberService) {}

  // 닉네임 중복 확인
  @Get('nickname/:nickname')
  checkNickname(@Param('nickname') nickname: string) {
    return this.memberService.checkNickname(nickname);
  }

  // 이메일 중복 확인
  @Get('email/:email')
  checkEmail(@Param('email') email: string) {
    return this.memberService.checkEmail(email);
  }

  // 회원가입
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
