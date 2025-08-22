package com.full.moon.domain.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.full.moon.domain.user.dto.LoginRequest;
import com.full.moon.domain.user.dto.SignUpRequest;
import com.full.moon.domain.user.service.UserService;
import com.full.moon.global.exception.BaseResponse;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;
import com.full.moon.global.security.token.dto.TokenResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name="유저 API 입니다.",description = "유저 관련된 것을 처리하는 API 모음입니다.")
public class UserController {

	private final UserService userService;

	//회원가입
	@PostMapping("/signup")
	@Operation(summary = "회원가입 메소드입니다.")
	public BaseResponse<TokenResponse> signupUser(@RequestBody SignUpRequest request){
		return BaseResponse.<TokenResponse>builder()
			.code(200)
			.data(userService.saveRealUser(request))
			.isSuccess(true)
			.message("회원가입에 성공하였습니다.").build();
	}


	//로그인
	@PostMapping("login")
	@Operation(summary = "로그인 메소드입니다.")
	public BaseResponse<TokenResponse> loginUser(@RequestBody LoginRequest request){
		return BaseResponse.<TokenResponse>builder()
			.code(200)
			.data(userService.loginWithGoogle(request))
			.isSuccess(true)
			.message("로그인에 성공하였습니다.").build();
	}

	//회원탈퇴
	@Operation(summary = "회원탈퇴 API")
	@DeleteMapping("/signout")
	public BaseResponse<Void> singOut(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
		return userService.signOutUser(customOAuth2User);
	}


	//로그아웃
	@Operation(summary = "로그아웃 API")
	@PostMapping("/logout")
	public BaseResponse<Void> logout(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
		return userService.logOutUser(customOAuth2User);
	}
	//회원 정보 수정
}
