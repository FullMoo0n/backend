package com.full.moon.domain.bookpage.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.full.moon.domain.book.entitiy.Book;
import com.full.moon.domain.book.repository.BookRepository;
import com.full.moon.domain.bookpage.dto.BookPageResponse;
import com.full.moon.domain.bookpage.entitiy.BookPage;
import com.full.moon.domain.bookpage.repository.BookPageRepository;
import com.full.moon.domain.child.entity.Child;
import com.full.moon.domain.child.repository.ChildRepository;
import com.full.moon.domain.user.entitiy.User;
import com.full.moon.global.exception.CustomException;
import com.full.moon.global.exception.ErrorCode;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;
import com.full.moon.global.utils.UserUtils;
import com.full.moon.infra.s3.service.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookPageService {

	private final BookPageRepository bookPageRepository;
	private final ChildRepository childRepository;
	private final BookRepository bookRepository;
	private final S3Service s3Service;
	private final UserUtils userUtils;

	public BookPageResponse makeNewBookPage(CustomOAuth2User customOAuth2User,Long childId, Long bookId, MultipartFile file){


		User user = userUtils.findUser(customOAuth2User);

		Child child = childRepository.findByIdAndUser(childId,user)
			.orElseThrow(()->new CustomException(ErrorCode.NO_CHILD));

		Book book = bookRepository.findByIdAndChild(bookId,child)
			.orElseThrow(()->new CustomException(ErrorCode.NO_BOOK));

		String imgUrl = s3Service.uploadFile(file);
		//여기서 FastAPI server로 요청보냄


		BookPage bookPage = BookPage.builder()
			.book(book)
			.originalImageUrl(imgUrl)
			.videoUrl("")
			.build();

		bookPageRepository.save(bookPage);

		return new BookPageResponse(bookPage.getId(),bookPage.getOriginalImageUrl(),bookPage.getVideoUrl());
	}


	public Void deleteBookPage(Long bookPageId){

		BookPage bookPage = bookPageRepository.findById(bookPageId)
			.orElseThrow(()->new CustomException(ErrorCode.NO_BOOKPAGE));

		bookPageRepository.delete(bookPage);
		return null;
	}


	public Page<BookPageResponse> getAllPage(Long bookId, Pageable pageable){

		Book book = bookRepository.findById(bookId)
			.orElseThrow(()->new CustomException(ErrorCode.NO_BOOK));


		return  bookPageRepository.findAllByBook(book,pageable)
			.map(bookPage -> new BookPageResponse(bookPage.getId(),bookPage.getOriginalImageUrl(),bookPage.getVideoUrl()));
	}
}
