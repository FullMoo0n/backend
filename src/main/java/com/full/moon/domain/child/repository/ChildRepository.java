package com.full.moon.domain.child.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.full.moon.domain.child.entity.Child;
import com.full.moon.domain.user.entitiy.User;

@Repository
public interface ChildRepository extends JpaRepository<Child,Long> {

	List<Child> findAllByUser(User user);
	Optional<Child> findByIdAndUser(Long id, User user);
}
