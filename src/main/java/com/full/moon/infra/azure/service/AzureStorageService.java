package com.full.moon.infra.azure.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.full.moon.global.exception.CustomException;
import com.full.moon.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureStorageService {

	@Value("${spring.cloud.azure.storage.blob.container-name}")
	private String containerName;

	private final BlobServiceClient blobServiceClient;

	public String uploadFile(MultipartFile file) {
		try {
			String originalFilename = file.getOriginalFilename();
			String extension = "";
			if (originalFilename != null && originalFilename.contains(".")) {
				extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			}
			String fileName = UUID.randomUUID() + extension;
			
			BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
			BlobClient blobClient = containerClient.getBlobClient(fileName);

			BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(file.getContentType());
			
			blobClient.upload(file.getInputStream(), file.getSize(), true);
			blobClient.setHttpHeaders(headers);

			return blobClient.getBlobUrl();

		} catch (IOException e) {
			log.error("Azure Storage upload error", e);
			throw new CustomException(ErrorCode.UPLOAD_FAIL);
		}
	}
}
