package com.full.moon.global.security.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.full.moon.global.security.token.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

	Void deleteByToken(String token);
	RefreshToken findByUserId(Long userId);
	void deleteByUserId(Long userId);
}
