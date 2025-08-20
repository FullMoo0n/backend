package com.full.moon.global.utils;

import org.springframework.stereotype.Component;

import com.full.moon.domain.user.entitiy.User;
import com.full.moon.domain.user.repository.UserRepository;
import com.full.moon.global.exception.CustomException;
import com.full.moon.global.exception.ErrorCode;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserUtils {

	private final UserRepository userRepository;

	public User findUser(CustomOAuth2User customOAuth2User){
		return userRepository.findById(Long.parseLong(customOAuth2User.getUserId()))
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
