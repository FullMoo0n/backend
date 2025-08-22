package com.full.moon.domain.book.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.full.moon.domain.book.dto.BookResponse;
import com.full.moon.domain.book.dto.BookTriple;
import com.full.moon.domain.book.entitiy.Book;
import com.full.moon.domain.book.repository.BookRepository;
import com.full.moon.domain.bookpage.entitiy.BookPage;
import com.full.moon.domain.bookpage.repository.BookPageRepository;
import com.full.moon.domain.child.entity.Child;
import com.full.moon.domain.child.repository.ChildRepository;
import com.full.moon.domain.user.entitiy.User;
import com.full.moon.domain.user.repository.UserRepository;
import com.full.moon.global.exception.CustomException;
import com.full.moon.global.exception.ErrorCode;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;
import com.full.moon.global.utils.UserUtils;
import com.full.moon.infra.s3.service.S3Service;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;
	private final S3Service s3Service;
	private final UserRepository userRepository;
	private final ChildRepository childRepository;
	private final UserUtils userUtils;
	private final BookPageRepository bookPageRepository;


	public BookResponse makeBook(CustomOAuth2User customOAuth2User, MultipartFile file, String title, Long childId){


		User user = userUtils.findUser(customOAuth2User);

		Child child = findChild(childId,user);

		String imgUrl = s3Service.uploadFile(file);

		Book book = Book.builder()
			.bookCoverUrl(imgUrl)
			.child(child)
			.title(title)
			.build();

		bookRepository.save(book);

		return new BookResponse(book.getId(),book.getTitle(),book.getBookCoverUrl());
	}


	public Void deleteBook(CustomOAuth2User customOAuth2User,Long childId, Long bookId){

		Child child = findChild(childId,userUtils.findUser(customOAuth2User));

		Book book = findBook(bookId,child);

		bookRepository.delete(book);

		return null;
	}

	public Page<BookResponse> getChildAllBook(CustomOAuth2User customOAuth2User, Long childId, Pageable pageable){

		Child child = findChild(childId,userUtils.findUser(customOAuth2User));

		return bookRepository.findAllByChild(child, pageable)
			.map(b -> new BookResponse(b.getId(), b.getTitle(), b.getBookCoverUrl()));
	}


	// public Page<BookResponse> getAllBook(CustomOAuth2User customOAuth2User,Pageable pageable){
	//
	// 	return bookRepository.findAll(pageable)
	// 		.map(book -> new BookResponse(book.getId(),book.getTitle(),book.getBookCoverUrl()));
	// }

	public Page<BookTriple> getAllBookGrouped(CustomOAuth2User user, Pageable groupPageable) {
		final int GROUP_SIZE = 3;

		// 클라이언트가 "묶음 페이지 크기"로 넘긴 값을 책 페이지 크기로 환산
		int groupsPerPage = groupPageable.getPageSize();      // 예: 6 묶음/페이지
		int booksPerPage  = groupsPerPage * GROUP_SIZE;       // 예: 18 권/페이지

		// 정렬 그대로 유지 (없으면 createdAt DESC 기본)
		Sort sort = groupPageable.getSort().isSorted()
			? groupPageable.getSort()
			: Sort.by(Sort.Direction.DESC, "createdAt");

		Pageable bookPageable = PageRequest.of(groupPageable.getPageNumber(), booksPerPage, sort);

		Page<Book> bookPage = bookRepository.findAll(bookPageable);

		// Book -> BookResponse
		List<BookResponse> list = bookPage.getContent().stream()
			.map(b -> new BookResponse(b.getId(), b.getTitle(), b.getBookCoverUrl()))
			.toList();

		// 페이지 내에서 랜덤 셔플
		Collections.shuffle(list);

		// 3개씩 묶기
		List<BookTriple> triples = new ArrayList<>();
		for (int i = 0; i < list.size(); i += GROUP_SIZE) {
			triples.add(new BookTriple(list.subList(i, Math.min(i + GROUP_SIZE, list.size()))));
		}

		// 전체 묶음 개수 = ceil(전체 책 수 / 3)
		long totalBooks  = bookPage.getTotalElements();
		long totalGroups = (totalBooks + GROUP_SIZE - 1) / GROUP_SIZE;

		return new PageImpl<>(triples, groupPageable, totalGroups);
	}



	@Transactional
	public Void shareBook(CustomOAuth2User customOAuth2User,  Long friendBook, Long childId){
		User user = userUtils.findUser(customOAuth2User);

		Child child = findChild(childId,user);

		Book book = bookRepository.findById(friendBook)
			.orElseThrow(()-> new CustomException(ErrorCode.NO_BOOK));

		List<BookPage> bookPages = bookPageRepository.findAllByBook(book);

		Book newBook = Book.builder()
			.child(child)
			.title(book.getTitle())
			.bookCoverUrl(book.getBookCoverUrl())
			.build();

		bookRepository.save(newBook);

		List<BookPage> newBookPages = bookPages.stream()
			.map(old ->{
				BookPage newBookPage =  BookPage.builder().videoUrl(old.getVideoUrl()).originalImageUrl(old.getOriginalImageUrl()).book(newBook).build();
				return bookPageRepository.save(newBookPage);
			})
			.toList();
		return null;
	}


	private Book findBook(Long bookId,Child child){
		return bookRepository.findByIdAndChild(bookId,child)
			.orElseThrow(()->new CustomException(ErrorCode.NO_BOOK));
	}



	private Child findChild(Long childId,User user){
		return childRepository.findByIdAndUser(childId,user)
			.orElseThrow(()-> new CustomException(ErrorCode.NO_CHILD));
	}
}

