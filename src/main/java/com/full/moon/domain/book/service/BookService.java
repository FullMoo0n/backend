package com.full.moon.domain.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.full.moon.domain.book.dto.BookResponse;
import com.full.moon.domain.book.entitiy.Book;
import com.full.moon.domain.book.repository.BookRepository;
import com.full.moon.domain.child.entity.Child;
import com.full.moon.domain.child.repository.ChildRepository;
import com.full.moon.domain.user.entitiy.User;
import com.full.moon.domain.user.repository.UserRepository;
import com.full.moon.global.exception.CustomException;
import com.full.moon.global.exception.ErrorCode;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;
import com.full.moon.global.utils.UserUtils;
import com.full.moon.infra.s3.service.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;
	private final S3Service s3Service;
	private final UserRepository userRepository;
	private final ChildRepository childRepository;
	private final UserUtils userUtils;


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


	public Page<BookResponse> getAllBook(CustomOAuth2User customOAuth2User,Pageable pageable){

		return bookRepository.findAll(pageable)
			.map(book -> new BookResponse(book.getId(),book.getTitle(),book.getBookCoverUrl()));
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

