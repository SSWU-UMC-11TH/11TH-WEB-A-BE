import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';

import { Book } from './entities/book.entity.js';
import { Category } from '../categories/entities/category.entity.js';
import { BookResponseDto, CreateBookDto } from './dto/book.dto.js';

@Injectable()
export class BookService {
  constructor(
    @InjectRepository(Book)
    private readonly bookRepository: Repository<Book>,
    @InjectRepository(Category)
    private readonly categoryRepository: Repository<Category>,
  ) {}

  // 실습 1: 전체 도서 조회
  async getAllBooks(): Promise<BookResponseDto[]> {
    const books = await this.bookRepository.find({
      relations: {
        category: true,
      },
      order: {
        bookId: 'DESC',
      },
    });

    return books.map((book) => BookResponseDto.from(book));
  }
  async createBook(dto: CreateBookDto): Promise<BookResponseDto> {
    const category = await this.categoryRepository.findOne({
      where: {
        categoryId: dto.categoryId,
      },
    });

    if (!category) {
      throw new NotFoundException('존재하지 않는 카테고리입니다.');
    }

    const book = this.bookRepository.create({
      category,
      title: dto.title,
      description: dto.description ?? null,
      isAvailable: true,
    });

    const savedBook = await this.bookRepository.save(book);

    return BookResponseDto.from(savedBook);
  }

  // 기존 카테고리별 조회 기능
  async findByCategory(categoryId: number): Promise<BookResponseDto[]> {
    const books = await this.bookRepository.find({
      where: {
        category: {
          categoryId,
        },
      },
      relations: {
        category: true,
      },
      order: {
        bookId: 'DESC',
      },
    });

    return books.map((book) => BookResponseDto.from(book));
  }
}
