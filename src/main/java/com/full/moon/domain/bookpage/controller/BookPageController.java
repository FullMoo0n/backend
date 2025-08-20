package com.full.moon.domain.bookpage.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.full.moon.domain.bookpage.dto.BookPageResponse;
import com.full.moon.domain.bookpage.service.BookPageService;
import com.full.moon.global.exception.BaseResponse;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookpage")
@Tag(name = "책 페이지와 관련된 API")
public class BookPageController {

	private final BookPageService bookPageService;

	//책 페이지를 추가
	@PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "책에 페이지를 추가하는 API")
	public BaseResponse<BookPageResponse> makePage(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,@RequestParam Long childId,
		@RequestParam Long bookId,
		@RequestParam("image") MultipartFile file){

		return BaseResponse.<BookPageResponse>builder()
			.code(201)
			.isSuccess(true)
			.message("책 페이지 등록에 성공하였습니다.")
			.data(bookPageService.makeNewBookPage(customOAuth2User, childId, bookId, file))
			.build();
	}

	//책 페이지 삭제
	@DeleteMapping("")
	@Operation(summary = "책 페이지를 삭제하는 API")
	public BaseResponse<Void> deletePage(@RequestParam Long bookPageId){

		return BaseResponse.<Void>builder()
			.message("책 페이지를 삭제하였습니다.")
			.isSuccess(true)
			.code(200)
			.data(bookPageService.deleteBookPage(bookPageId))
			.build();

	}

	//책에 해당하는 모든 책 페이지
	@GetMapping("")
	@Operation(summary = "책에 해당하는 모든 책 페이지를 가져오는 API")
	public BaseResponse<?> getAllPages( @RequestParam Long bookId,@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
	Pageable pageable){
		return BaseResponse.builder()
			.isSuccess(true)
			.code(200)
			.message("책에 해당하는 모든 페이지를 가져왔습니다.")
			.data(bookPageService.getAllPage(bookId,pageable))
			.build();
	}

}
