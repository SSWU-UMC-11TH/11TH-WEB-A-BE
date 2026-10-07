package com.umc.week03.service;

import com.umc.week03.domain.dto.BookResponse;
import com.umc.week03.domain.dto.CreateBookRequest;
import com.umc.week03.domain.entity.Book;
import com.umc.week03.domain.entity.Category;
import com.umc.week03.repository.BookRepository;
import com.umc.week03.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAllByOrderByBookIdDesc()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 카테고리입니다.")
                );

        Book book = new Book(
                category,
                request.title(),
                request.description()
        );

        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingOrderByBookIdDesc(keyword)
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryCategoryId(categoryId)
                .stream()
                .map(BookResponse::from)
                .toList();
    }
}