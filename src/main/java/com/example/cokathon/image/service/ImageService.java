package com.example.cokathon.image.service;

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
import com.example.cokathon.image.service.s3.S3Uploader;

import lombok.RequiredArgsConstructor;

public interface ImageService {

	ImageIdResponse addImage(final MultipartFile file);

	void deleteImage(final long imageId);

	ImageIdResponse addFaceImage(final MultipartFile file);

	void deleteFaceImage(final long imageId);

	ImageUrlListResponse getImageUrls();

	ImageUrlListResponse getFaceImageUrls();

}
