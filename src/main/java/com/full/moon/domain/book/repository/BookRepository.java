package com.full.moon.domain.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.full.moon.domain.book.entitiy.Book;
import com.full.moon.domain.child.entity.Child;

public interface BookRepository extends JpaRepository<Book,Long> {

	Optional<Book> findByIdAndChild(Long bookId, Child child);

	List<Book> findAllByChild(Child child);


	Page<Book> findAllByChild(Child child, Pageable pageable);


}
