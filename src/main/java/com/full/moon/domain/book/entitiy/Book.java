package com.full.moon.domain.book.entitiy;

import com.full.moon.domain.child.entity.Child;
import com.full.moon.domain.user.entitiy.User;
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
@NoArgsConstructor
@Getter
public class Book extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//book cover는 생성형 따로 호출
	private String bookCoverUrl;

	private String title;

	@ManyToOne
	@JoinColumn(name = "child_id")
	private Child child;

	@Builder
	private Book(String bookCoverUrl, String title, Child child) {
		this.bookCoverUrl = bookCoverUrl;
		this.title = title;
		this.child = child;
	}
}
