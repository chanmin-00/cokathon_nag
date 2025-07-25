package com.example.cokathon.image.service.impl;

import static com.example.cokathon.image.exception.ImageErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.cokathon.image.domain.Image;
import com.example.cokathon.image.dto.ImageDTO;
import com.example.cokathon.image.dto.S3InfoDTO;
import com.example.cokathon.image.dto.response.ImageIdResponse;
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

	private static final String TEMP_FOLDER = "temp";

	@Override
	public ImageIdResponse addImageToTemp(final MultipartFile file) {
		S3InfoDTO s3InfoDTO = s3Uploader.uploadFiles(file, TEMP_FOLDER);
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

}
