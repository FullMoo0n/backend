package com.full.moon.domain.user.service;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.full.moon.domain.user.dto.LoginErrorResponse;
import com.full.moon.domain.user.entitiy.Domain;
import com.full.moon.domain.user.entitiy.User;
import com.full.moon.domain.user.repository.UserRepository;
import com.full.moon.global.exception.CustomException;
import com.full.moon.global.exception.ErrorCode;
import com.full.moon.global.exception.LoginException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignService {

	private final UserRepository userRepository;

	@Transactional
	public User saveUser(String email, String domain){

		if(!userRepository.existsByEmail(email)){
			throw new LoginException(
				HttpStatus.CREATED,201,"유저가 존재하지 않습니다. 회원가입이 필요합니다.",new LoginErrorResponse(email,domain));
		}

		return userRepository.findByEmail(email)
			.orElseThrow(()->new CustomException(ErrorCode.USER_NOT_FOUND));
	}
}
