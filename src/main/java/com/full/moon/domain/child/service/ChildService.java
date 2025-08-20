package com.full.moon.domain.child.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.full.moon.domain.child.dto.ChildAllResponse;
import com.full.moon.domain.child.dto.ChildResponse;
import com.full.moon.domain.child.entity.Child;
import com.full.moon.domain.child.repository.ChildRepository;
import com.full.moon.domain.user.entitiy.User;
import com.full.moon.domain.user.repository.UserRepository;
import com.full.moon.global.exception.CustomException;
import com.full.moon.global.exception.ErrorCode;
import com.full.moon.global.security.oauth.entity.CustomOAuth2User;
import com.full.moon.infra.s3.service.S3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChildService {

	private final ChildRepository childRepository;
	private final UserRepository userRepository;
	private final S3Service s3Service;


	public ChildResponse makeChild(CustomOAuth2User customOAuth2User, MultipartFile file, String name){

		User user =  userRepository.findById(Long.parseLong(customOAuth2User.getUserId()))
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

		String imgUrl = s3Service.uploadFile(file);

		Child child = Child.builder()
			.name(name)
			.user(user)
			.photoUrl(imgUrl)
			.build();

		childRepository.save(child);

		return new ChildResponse(child.getId(),child.getName(),child.getPhotoUrl());
	}


	public ChildAllResponse getChildren(CustomOAuth2User customOAuth2User){
		User user =  userRepository.findById(Long.parseLong(customOAuth2User.getUserId()))
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

		List<ChildResponse> children = childRepository.findAllByUser(user)
			.stream().map(child->  new ChildResponse(child.getId(),child.getName(),child.getPhotoUrl())).toList();

		return new ChildAllResponse(children);
	}

	public Void deleteChild(CustomOAuth2User customOAuth2User, Long childId){
		User user =  userRepository.findById(Long.parseLong(customOAuth2User.getUserId()))
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

		Child child = childRepository.findByIdAndUser(childId,user)
			.orElseThrow(()->new CustomException(ErrorCode.NO_CHILD));

		childRepository.delete(child);
		return null;
	}


	public ChildResponse updateChild(CustomOAuth2User customOAuth2User, MultipartFile file, String name ,Long childId){
		User user =  userRepository.findById(Long.parseLong(customOAuth2User.getUserId()))
			.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));


		Child child = childRepository.findByIdAndUser(childId,user)
			.orElseThrow(()->new CustomException(ErrorCode.NO_CHILD));

		String imgUrl = s3Service.uploadFile(file);

		child.setName(name);
		child.setPhotoUrl(imgUrl);

		childRepository.save(child);

		return new ChildResponse(childId,name,imgUrl);
	}



}
