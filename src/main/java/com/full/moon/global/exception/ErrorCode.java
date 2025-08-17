package com.full.moon.global.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	ALREADY_SIGNUP(BAD_REQUEST,410,"이미 가입된 회원입니다."),


	INVALID_PASSWORD(BAD_REQUEST,411,"잘못된 비밀번호입니다."),

	UPLOAD_FAIL(BAD_REQUEST,412,"업로드에 실패하였습니다."),

	NO_CHILD(BAD_REQUEST,413,"존재하지 않는 어린이입니다."),
	NO_SURVEY(BAD_REQUEST,414,"존재하지 않는 설문입니다."),
	NO_ADVICE(BAD_REQUEST,404,"존재하지 않는 상담입니다."),

	JSON_SERIALIZE_FAIL(BAD_REQUEST,415,"JSON 파싱 에러"),
	OPENAI_NO_CONTENT(BAD_REQUEST,416,"잘못된 OPENAI 내용입니다."),
	OPENAI_HTTP_ERROR(BAD_REQUEST,417,"OPENAI 통신 오류"),
	OPENAI_EMPTY_BODY(BAD_REQUEST,418, "OPENAI 비어있는 값 오류"),
	OPENAI_COMM_FAIL(BAD_REQUEST,419,"OPENAI 일반적인 오류"),
	OPENAI_PARSE_FAIL(BAD_REQUEST,420,"OPENAI 파싱 오류"),





	LOGIN_FAIL(HttpStatus.BAD_REQUEST,400,"로그인에 오류가 발생하였습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,500,"서버에 오류가 발생하였습니다."),
	JWT_KEY_GENERATION_FAILED(HttpStatus.BAD_REQUEST,400,"JWT 키 생성에 실패하였습니다."),
	NO_REFRESH_TOKEN(UNAUTHORIZED,400, "리프레시 토큰이 없습니다."),
	LOGOUT_ERROR(BAD_REQUEST,400,"로그아웃에 실패하였습니다."),
	SIGNUP_ERROR(BAD_REQUEST,400,"회원가입에러입니다."),
	EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, 400,"만료된 토큰입니다."),
	JWT_PARSE_FAILED(BAD_REQUEST,404,"토큰 파싱이 잘못되었습니다."),

	KAKAO_USER_ERROR(BAD_REQUEST,404,"카카오 유저 정보를 가져오지 못하였습니다."),
	TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,500, "토큰을 제대로 생성하지 못하였습니다."),


	USER_NOT_FOUND(BAD_REQUEST,404,"존재하지 않는 유저입니다.");



	private final HttpStatus status;
	private final int code;
	private final String message;


	ErrorCode( HttpStatus status,int code, String message){
		this.code = code;
		this.status = status;
		this.message = message;
	}
}
