package com.full.moon.domain.book.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.full.moon.domain.book.dto.BookResponse;
import com.full.moon.domain.book.dto.BookTriple;
import com.full.moon.domain.book.service.BookService;
import com.full.moon.global.exception.BaseResponse;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/book")
@Tag(name = "책과 관련된 API")
public class BookController {

	private final BookService bookService;

	//책 추가
	@PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "책을 추가하는 API")
	public BaseResponse<BookResponse> makeBook(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,@RequestParam("image")
	MultipartFile file,@RequestParam String title,@RequestParam Long childId){

		return BaseResponse.<BookResponse>builder()
			.message("책 추가에 성공하였습니다.")
			.isSuccess(true)
			.code(201)
			.data(bookService.makeBook(customOAuth2User, file, title, childId))
			.build();
	}

	//책 삭제
	@DeleteMapping("")
	@Operation(summary = "책을 삭제하는 API")
	public BaseResponse<Void> deleteBook(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,@RequestParam Long childId, @RequestParam Long bookId){

		return BaseResponse.<Void>builder()
			.code(200)
			.isSuccess(true)
			.message("책을 삭제하였습니다.")
			.data(bookService.deleteBook(customOAuth2User, childId, bookId))
			.build();
	}

	//다른 사람 책을 자신의 책 꽂이에 추가
	@PostMapping("/share")
	@Operation(summary = "다른 사람 책을 자신의 책 꽂이에 추가")
	public BaseResponse<Void> getOtherBook(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,@RequestParam Long friendsBookId,@RequestParam Long childId){

		return BaseResponse.<Void>builder()
			.code(200)
			.message("다른 사람을 책을 공유받았습니다.")
			.isSuccess(true)
			.data(bookService.shareBook(customOAuth2User, friendsBookId, childId))
			.build();
	}


	//갖고 있는 책 전체적으로 가져오기
	@GetMapping("/children/{childId}")
	@Operation(summary = "아이가 갖고 있는 책들을 가져오는 API")
	public BaseResponse<Page<BookResponse>> getChildAllBook(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
		@PathVariable Long childId,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
		Pageable pageable
		){
		return BaseResponse.<Page<BookResponse>>builder()
			.isSuccess(true)
			.code(200)
			.message("아이가 갖고 있는 책들을 가져왔습니다.")
			.data(bookService.getChildAllBook(customOAuth2User, childId, pageable))
			.build();
	}

	//모든 책 가져오기
	@GetMapping("")
	@Operation(summary = "소유주 상관없이 모든 책을 3개 묶음으로 랜덤 제공")
	public BaseResponse<List<BookResponse>> getAllBook() {

		return BaseResponse.<List<BookResponse>>builder()
			.isSuccess(true)
			.code(200)
			.message("소유주 상관없이 모든 책을 3개 묶음으로 랜덤 제공했습니다.")
			.data(bookService.getRandom3Books())
			.build();
	}


}
