package com.full.moon.domain.bookpage.service;

import java.io.IOException;
import java.time.Duration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.full.moon.domain.book.entitiy.Book;
import com.full.moon.domain.book.repository.BookRepository;
import com.full.moon.domain.bookpage.dto.BookPageResponse;
import com.full.moon.domain.bookpage.dto.ImageProcessRequest;
import com.full.moon.domain.bookpage.dto.ImageProcessResponse;
import com.full.moon.domain.bookpage.dto.VideoResponse;
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
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
public class BookPageService {

	private final BookPageRepository bookPageRepository;
	private final ChildRepository childRepository;
	private final BookRepository bookRepository;
	private final S3Service s3Service;
	private final UserUtils userUtils;
	private final WebClient fastApiWebClient;

	public BookPageResponse makeNewBookPage(CustomOAuth2User customOAuth2User,Long childId, Long bookId, MultipartFile file){


		User user = userUtils.findUser(customOAuth2User);

		Child child = childRepository.findByIdAndUser(childId,user)
			.orElseThrow(()->new CustomException(ErrorCode.NO_CHILD));

		Book book = bookRepository.findByIdAndChild(bookId,child)
			.orElseThrow(()->new CustomException(ErrorCode.NO_BOOK));

		String imgUrl = s3Service.uploadFile(file);
		//여기서 FastAPI server로 요청보냄

		String videoUrl = processBlocking(imgUrl);

		BookPage bookPage = BookPage.builder()
			.book(book)
			.originalImageUrl(imgUrl)
			.videoUrl(videoUrl)
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

	private String processImageToVideos(String s3ImageUrl) {
		ImageProcessRequest request = new ImageProcessRequest(s3ImageUrl);

		VideoResponse response = fastApiWebClient.post()
			.uri("/process-image-to-videos")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.retrieve()
			.bodyToMono(VideoResponse.class)
			.block(); // 동기 처리

		if (response == null || response.video_urls() == null || response.video_urls().isEmpty()) {
			throw new RuntimeException("FastAPI 응답에서 video_urls를 찾을 수 없습니다.");
		}

		// 첫 번째 URL만 반환
		return response.video_urls().get(0);
	}

	private String processBlocking(String s3ImageUrl) {
		ImageProcessRequest request = new ImageProcessRequest(s3ImageUrl);

		VideoResponse res = fastApiWebClient.post()
			.uri("/process-image-to-videos")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(request)
			.retrieve()
			.bodyToMono(VideoResponse.class)
			.timeout(Duration.ofMinutes(6)) // 이 레벨에서도 가드
			.block();

		if (res == null || res.video_urls() == null || res.video_urls().isEmpty()) {
			throw new IllegalStateException("FastAPI 응답에 video_urls가 없습니다.");
		}
		return res.video_urls().get(0); // 첫 번째만 사용
	}

}
