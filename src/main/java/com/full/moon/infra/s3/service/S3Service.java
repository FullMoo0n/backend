package com.full.moon.infra.s3.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.full.moon.global.exception.CustomException;
import com.full.moon.global.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
public class S3Service {
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${cloud.aws.region.static}")
	private String region;

	@Value("${cloud.aws.credentials.access-key}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key}")
	private String secretKey;

	public String uploadFile(MultipartFile file) {
		S3Client s3 = S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(accessKey, secretKey)))
			.build();

		try {
			String rawName = file.getOriginalFilename();              // 원본 파일명
			String safeName = URLEncoder.encode(rawName, StandardCharsets.UTF_8)
				.replace("+", "%20");                             // 공백을 %20로
			String key = "test_images/" + UUID.randomUUID() + "_" + safeName;

			PutObjectRequest put = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType(file.getContentType())
				// 공개 URL로 바로 접근하려면 주석 해제
				// .acl(ObjectCannedACL.PUBLIC_READ)
				.build();

			s3.putObject(put, software.amazon.awssdk.core.sync.RequestBody
				.fromInputStream(file.getInputStream(), file.getSize()));

			// ⚠️ 여기서는 절대 다시 인코딩하지 말 것
			String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
			return fileUrl;

		} catch (S3Exception | IOException e) {
			log.error("S3 upload error", e);
			throw new CustomException(ErrorCode.UPLOAD_FAIL);
		} finally {
			s3.close();
		}
	}


	public String uploadBytes(byte[] bytes, String contentType, String originalFilename) {
		S3Client s3 = S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(accessKey, secretKey)
			))
			.build();

		try {
			String fileName = "uploads/" + UUID.randomUUID() + "_" + originalFilename;

			PutObjectRequest put = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(fileName)
				.contentType(contentType)
				.cacheControl("public, max-age=31536000")
				.build();

			s3.putObject(put, software.amazon.awssdk.core.sync.RequestBody.fromBytes(bytes));


			String fileUrl = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
			return fileUrl;

		} catch (S3Exception e) {
			log.error("S3 upload error", e);
			throw new CustomException(ErrorCode.UPLOAD_FAIL);
		} finally {
			s3.close();
		}
	}
}


