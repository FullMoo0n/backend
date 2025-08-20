package com.full.moon.domain.child.controller;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.full.moon.domain.child.dto.ChildAllResponse;
import com.full.moon.domain.child.dto.ChildResponse;
import com.full.moon.domain.child.service.ChildService;
import com.full.moon.global.exception.BaseResponse;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/child")
@RequiredArgsConstructor
public class ChildController {

	private final ChildService childService;

	//아이 프로필 등록
	@PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "아이를 추가하는 API")
	public BaseResponse<ChildResponse> makeChild(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,@RequestParam("image")
		MultipartFile file,@RequestParam String name){
		return BaseResponse.<ChildResponse>builder()
					.message("아이를 추가하였습니다.")
					.isSuccess(true)
					.code(201)
					.data(childService.makeChild(customOAuth2User,file,name)).build();
	}

	//모든 아이 프로필 조회
	@GetMapping()
	@Operation(summary = "부모가 가진 모든 아이를 조회하는 API")
	public BaseResponse<ChildAllResponse> getAllChild(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){

		return BaseResponse.<ChildAllResponse>builder()
			.isSuccess(true)
			.code(200)
			.message("전체 아이를 조회하였습니다.")
			.data(childService.getChildren(customOAuth2User))
			.build();
	}

	//아이 프로필 편집
	@PatchMapping(value = "update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "아이의 정보를 편집할 때 사용하는 API (아마 : 사진이랑 이름 변경만 할듯?)")
	public BaseResponse<ChildResponse>  updateChild(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,@RequestParam("image")
	MultipartFile file,@RequestParam String name,@RequestParam Long childId){

		return BaseResponse.<ChildResponse>builder()
			.code(200)
			.isSuccess(true)
			.message("아이 프로필이 수정되었습니다.")
			.data(childService.updateChild(customOAuth2User,file,name,childId))
			.build();

	}
	//아이 프로필 제거
	@DeleteMapping()
	@Operation(summary = "아이 리스트에서 아이를 제거할 때 쓰는 API")
	public BaseResponse<Void> deleteChild(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,@RequestParam Long childId){

		childService.deleteChild(customOAuth2User,childId);
		return BaseResponse.<Void>builder()
			.isSuccess(true)
			.code(200)
			.message("아이가 제거되었습니다.")
			.data(null)
			.build();
	}

}
