package com.example.cokathon.image.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.cokathon.image.dto.response.ImageIdResponse;

public interface ImageService {

	ImageIdResponse addImageToTemp(final MultipartFile file);

	void deleteImage(final long imageId);
}
