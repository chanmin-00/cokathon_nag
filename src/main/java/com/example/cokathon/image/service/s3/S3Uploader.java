package com.example.cokathon.image.service.s3;

import static com.example.cokathon.image.exception.ImageErrorCode.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.cokathon.image.dto.S3InfoDTO;
import com.example.cokathon.image.exception.ImageException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
	private final AmazonS3Client amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	/**
	 * 이미지 파일을 S3에 업로드하고 정보 반환
	 */
	public S3InfoDTO uploadFiles(MultipartFile multipartFile, String folderName) throws ImageException {
		File localFile = convertMultipartFile(multipartFile);
		return uploadFileToS3(localFile, folderName);
	}

	/**
	 * S3에서 파일 삭제
	 */
	public void deleteFile(S3InfoDTO s3Info) {
		String fileName = buildFilePath(s3Info.folderName(), s3Info.fileName());
		amazonS3.deleteObject(bucket, fileName);
	}

	/**
	 * S3 업로드 + 로컬 파일 삭제
	 */
	private S3InfoDTO uploadFileToS3(File file, String folderName) {
		String fileName = buildFilePath(folderName, file.getName());

		// S3 업로드
		PutObjectRequest request = new PutObjectRequest(bucket, fileName, file)
			.withCannedAcl(CannedAccessControlList.PublicRead);
		amazonS3.putObject(request);

		// 업로드된 URL 획득
		String url = amazonS3.getUrl(bucket, fileName).toString();

		// 로컬 파일 삭제
		if (!file.delete()) {
			log.error("Failed to delete local file: {}", file.getAbsolutePath());
		}

		return S3InfoDTO.of(folderName, file.getName(), url);
	}

	/**
	 * MultipartFile을 로컬 임시 File로 변환
	 */
	private File convertMultipartFile(MultipartFile file) throws ImageException {
		String extension = extractExtension(file);
		String tempFileName = UUID.randomUUID() + "." + extension;
		File localFile = new File(System.getProperty("user.dir"), tempFileName);

		try (FileOutputStream fos = new FileOutputStream(localFile)) {
			fos.write(file.getBytes());
		} catch (IOException e) {
			throw ImageException.from(IMAGE_PROCESSING_FAIL);
		}

		return localFile;
	}

	/**
	 * 파일 확장자 추출
	 */
	private String extractExtension(MultipartFile file) throws ImageException {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || !originalFilename.contains(".")) {
			throw ImageException.from(IMAGE_NOT_FOUND);
		}
		return originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
	}

	/**
	 * S3 내 파일 경로 생성
	 */
	private String buildFilePath(String folder, String filename) {
		return folder + "/" + filename;
	}
}
