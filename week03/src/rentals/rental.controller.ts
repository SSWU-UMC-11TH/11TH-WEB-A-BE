import { Controller, Post, Body } from '@nestjs/common';
import { RentalService } from './rental.service.js';

@Controller('rentals')
export class RentalController {
  constructor(private readonly rentalService: RentalService) {}

  @Post()
  async createRental(@Body() body: { userId: number; bookId: number }) {
    return await this.rentalService.createRental(body.userId, body.bookId);
  }
}
