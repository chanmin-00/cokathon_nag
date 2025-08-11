package com.example.bojmate.image.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.bojmate.image.dto.response.ImageIdResponse;
import com.example.bojmate.image.dto.response.ImageUrlListResponse;

public interface ImageService {

	ImageIdResponse addImage(final MultipartFile file);

	void deleteImage(final long imageId);

	ImageIdResponse addFaceImage(final MultipartFile file);

	void deleteFaceImage(final long imageId);

	ImageUrlListResponse getImageUrls();

	ImageUrlListResponse getFaceImageUrls();

}
