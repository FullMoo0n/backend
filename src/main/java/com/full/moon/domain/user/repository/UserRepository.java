package com.full.moon.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.full.moon.domain.user.entitiy.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {


	Boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
}
