package com.full.moon.domain.child.entity;

import com.full.moon.domain.user.entitiy.User;
import com.full.moon.global.time.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "child")
public class Child  extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	private String photoUrl;

	@Setter
	private String name;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;


	@Builder
	private Child (String photoUrl, String name, User user){
		this.photoUrl = photoUrl;
		this.name = name;
		this.user = user;
	}

}
