package com.full.moon.global.exception;

import org.springframework.http.HttpStatus;

import com.full.moon.domain.user.dto.LoginErrorResponse;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException{
	private  HttpStatus status;
	private  int code;
	private  String message;
	private LoginErrorResponse errorResponse;

	public LoginException(HttpStatus status, int code , String message,LoginErrorResponse errorResponse){
		super(message);
		this.code = code;
		this.status =status;
		this.message = message;
		this.errorResponse = errorResponse;
	}

}
