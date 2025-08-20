package com.full.moon.domain.bookpage.entitiy;

import com.full.moon.domain.book.entitiy.Book;
import com.full.moon.global.time.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BookPage extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String originalImageUrl;

	private String videoUrl;

	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;

	@Builder
	private BookPage(String originalImageUrl,String videoUrl, Book book) {
		this.originalImageUrl = originalImageUrl;
		this.videoUrl = videoUrl;
		this.book = book;
	}
}
