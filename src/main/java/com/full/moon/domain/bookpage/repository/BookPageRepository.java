package com.full.moon.domain.bookpage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.full.moon.domain.book.entitiy.Book;
import com.full.moon.domain.bookpage.entitiy.BookPage;

public interface BookPageRepository extends JpaRepository<BookPage, Long> {

	Page<BookPage> findAllByBook(Book book, Pageable pageable);
}
