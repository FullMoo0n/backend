package com.full.moon.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class GoogleUserResponse {


	@JsonAlias({"sub", "id"})
	private String sub;

	private String email;

	@JsonAlias({"email_verified", "verified_email"})
	private Boolean emailVerified;

	private String picture;
}
