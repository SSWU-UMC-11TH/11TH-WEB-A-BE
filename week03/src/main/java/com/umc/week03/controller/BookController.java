package com.umc.week03.controller;

import com.umc.week03.domain.dto.BookResponse;
import com.umc.week03.domain.dto.CreateBookRequest;
import com.umc.week03.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookResponse> getBooks(
            @RequestParam(required = false) String keyword
    ) {
        if (keyword != null && !keyword.isBlank()) {
            return bookService.searchBooks(keyword);
        }

        return bookService.getAllBooks();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(
            @Valid @RequestBody CreateBookRequest request
    ) {
        return bookService.createBook(request);
    }

    @GetMapping("/category/{categoryId}")
    public List<BookResponse> getBooksByCategory(
            @PathVariable Long categoryId
    ) {
        return bookService.getBooksByCategoryId(categoryId);
    }
}
