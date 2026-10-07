package com.umc.week03.domain.repository;

import com.umc.week03.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAllByOrderByBookIdDesc();
    List<Book> findByTitleContainingOrderByBookIdDesc(String keyword);

    List<Book> findByCategoryCategoryId(Long categoryId);
}
