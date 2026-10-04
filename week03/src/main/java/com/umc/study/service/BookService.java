package com.umc.study.service;

import com.umc.study.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    // 전체 도서 조회
    public List<Map<String, Object>> getAllBooks() {
        return bookRepository.findAll();
    }

    // 특정 카테고리 도서 조회
    public List<Map<String, Object>> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    // 신규 도서 등록
    public void createBook(Map<String, Object> body) {
        bookRepository.save(body);
    }
}