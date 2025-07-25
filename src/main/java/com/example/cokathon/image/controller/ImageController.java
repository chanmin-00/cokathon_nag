package com.example.cokathon.image.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.cokathon.global.dto.DataResponse;
import com.example.cokathon.image.dto.response.ImageIdResponse;
import com.example.cokathon.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/image")
@RestController
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;

	@PostMapping(consumes = "multipart/form-data")
	public ResponseEntity<DataResponse<ImageIdResponse>> addImageToTemp(
		final @RequestParam MultipartFile file) {

		return ResponseEntity.ok(DataResponse.from(imageService.addImageToTemp(file)));
	}

	@DeleteMapping("/{imageId}")
	public ResponseEntity<DataResponse<Void>> deleteImage(final @PathVariable("imageId") long imageId) {

		imageService.deleteImage(imageId);
		return ResponseEntity.ok(DataResponse.ok());
	}
}
