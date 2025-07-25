package com.example.cokathon.image.service.impl;

import static com.example.cokathon.image.exception.ImageErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.cokathon.image.domain.Image;
import com.example.cokathon.image.dto.ImageDTO;
import com.example.cokathon.image.dto.S3InfoDTO;
import com.example.cokathon.image.dto.response.ImageIdResponse;
import com.example.cokathon.image.dto.response.ImageUrlListResponse;
import com.example.cokathon.image.exception.ImageException;
import com.example.cokathon.image.repository.ImageRepository;
import com.example.cokathon.image.service.ImageService;
import com.example.cokathon.image.service.s3.S3Uploader;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

	private final ImageRepository imageRepository;

	private final S3Uploader s3Uploader;

	private static final String BACKGROUND_FOLDER = "background";
	private static final String FACE_FOLDER = "face";

	@Override
	public ImageIdResponse addImage(final MultipartFile file) {
		S3InfoDTO s3InfoDTO = s3Uploader.uploadFiles(file, BACKGROUND_FOLDER);
		Image image = imageRepository.save(Image.from(ImageDTO.from(s3InfoDTO)));
		return ImageIdResponse.from(image.getId());
	}

	@Override
	public void deleteImage(final long imageId) {
		Image image = imageRepository.findById(imageId)
			.orElseThrow(() -> ImageException.from(IMAGE_NOT_FOUND));
		s3Uploader.deleteFile(S3InfoDTO.from(image.getS3Info()));
		imageRepository.delete(image);
	}

	@Override
	public ImageIdResponse addFaceImage(final MultipartFile file) {
		S3InfoDTO s3InfoDTO = s3Uploader.uploadFiles(file, FACE_FOLDER);
		Image image = imageRepository.save(Image.from(ImageDTO.from(s3InfoDTO)));
		return ImageIdResponse.from(image.getId());
	}

	@Override
	public void deleteFaceImage(final long imageId) {
		Image image = imageRepository.findById(imageId)
			.orElseThrow(() -> ImageException.from(IMAGE_NOT_FOUND));
		s3Uploader.deleteFile(S3InfoDTO.from(image.getS3Info()));
		imageRepository.delete(image);
	}

	// background 폴더의 이미지 URL을 반환하는 메서드
	@Override
	public ImageUrlListResponse getImageUrls() {

		return ImageUrlListResponse.from(
			imageRepository.findAllByS3InfoFolderName(BACKGROUND_FOLDER)
				.stream()
				.map(image -> image.getS3Info().getUrl())
				.toList()
		);
	}

	// face 폴더의 이미지 URL을 반환하는 메서드
	@Override
	public ImageUrlListResponse getFaceImageUrls() {
		return ImageUrlListResponse.from(
			imageRepository.findAllByS3InfoFolderName(FACE_FOLDER)
				.stream()
				.map(image -> image.getS3Info().getUrl())
				.toList()
		);
	}
}
