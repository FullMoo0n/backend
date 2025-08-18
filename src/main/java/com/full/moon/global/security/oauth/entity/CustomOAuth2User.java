package com.full.moon.global.security.oauth.entity;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

public class CustomOAuth2User implements OAuth2User {

	private final Map<String, Object> attributes;

	private final String uuid;

	@Getter
	private final String role;

	public CustomOAuth2User(Map<String, Object> attributes, String uuid, String role) {
		this.attributes = attributes;
		this.uuid = uuid;
		this.role = role;
	}

	public String getUserId() {
		return uuid;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return uuid;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(() -> role);
	}

}