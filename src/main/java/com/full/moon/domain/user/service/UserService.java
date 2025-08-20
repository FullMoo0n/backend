package com.full.moon.domain.user.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.full.moon.domain.user.dto.GoogleUserResponse;
import com.full.moon.domain.user.dto.LoginRequest;
import com.full.moon.domain.user.dto.SignUpRequest;
import com.full.moon.domain.user.entitiy.Domain;
import com.full.moon.domain.user.entitiy.User;
import com.full.moon.domain.user.entitiy.UserRole;
import com.full.moon.domain.user.repository.UserRepository;
import com.full.moon.global.exception.BaseResponse;
import com.full.moon.global.exception.CustomException;
import com.full.moon.global.exception.ErrorCode;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;
import com.full.moon.global.security.token.dto.TokenResponse;
import com.full.moon.global.security.token.entity.RefreshToken;
import com.full.moon.global.security.token.repository.RefreshTokenRepository;
import com.full.moon.global.security.token.service.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final RestTemplate restTemplate;
	private final JwtTokenProvider jwtTokenProvider;
	private final SignService signService;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final RefreshTokenRepository refreshTokenRepository;

	String domain = "GOOGLE";



	public TokenResponse loginWithGoogle(LoginRequest request){
		User user = signService.saveUser(request.email(), domain);
		return jwtTokenProvider.createToken(user.getId().toString());
	}

	private GoogleUserResponse getUserInfo(String accessToken) {
		String url = "https://www.googleapis.com/oauth2/v2/userinfo";

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		HttpEntity<Void> request = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {

				log.info(response.getBody());
				return objectMapper.readValue(response.getBody(), GoogleUserResponse.class);
			} else {
				throw new CustomException(ErrorCode.USER_NOT_FOUND);
			}
		} catch (Exception e) {
			throw new CustomException(ErrorCode.GOOGLE_USER_ERROR);
		}
	}

	@Transactional
	public TokenResponse saveRealUser(SignUpRequest  request){

		if(userRepository.existsByEmail(request.email())){
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}

		User user = User.builder()
			.userRole(UserRole.USER)
			.email(request.email())
			.name(request.name())
			.provider(Domain.GOOGLE)
			.build();

		userRepository.save(user);

		return jwtTokenProvider.createToken(user.getId().toString());
	}


	@Transactional
	public BaseResponse<Void> logOutUser(CustomOAuth2User customOAuth2User){

		Long userId = Long.parseLong(customOAuth2User.getUserId());
		RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId);

		return BaseResponse.<Void>builder()
			.code(200)
			.message("로그아웃이 완료되었습니다.")
			.data(refreshTokenRepository.deleteByToken(refreshToken.getToken()))
			.isSuccess(true)
			.build();
	}


	@Transactional
	public BaseResponse<Void> signOutUser(CustomOAuth2User customOAuth2User){
		Long userId = Long.parseLong(customOAuth2User.getUserId());
		RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId);

		refreshTokenRepository.deleteByToken(refreshToken.getToken());
		User user = userRepository.findById(userId)
			.orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));

		userRepository.delete(user);

		return BaseResponse.<Void>builder()
			.isSuccess(true)
			.code(200)
			.message("회원탈퇴가 완료되었습니다.")
			.data(null)
			.build();
	}

}
