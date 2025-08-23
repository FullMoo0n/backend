package com.full.moon.domain.bookpage.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.full.moon.domain.book.entitiy.Book;
import com.full.moon.domain.bookpage.entitiy.BookPage;

public interface BookPageRepository extends JpaRepository<BookPage, Long> {

	Page<BookPage> findAllByBook(Book book, Pageable pageable);
	List<BookPage> findAllByBook(Book book);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("delete from BookPage p where p.book.id = :bookId")
	void deleteByBookId(@Param("bookId") Long bookId);

}
