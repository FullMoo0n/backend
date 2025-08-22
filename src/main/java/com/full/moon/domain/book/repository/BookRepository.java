package com.full.moon.domain.book.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.full.moon.domain.book.entitiy.Book;
import com.full.moon.domain.child.entity.Child;

public interface BookRepository extends JpaRepository<Book,Long> {

	Optional<Book> findByIdAndChild(Long bookId, Child child);

	List<Book> findAllByChild(Child child);


	Page<Book> findAllByChild(Child child, Pageable pageable);


	@Query(value = "SELECT * FROM book ORDER BY RAND() LIMIT 3", nativeQuery = true)
	List<Book> findRandom3();
}
