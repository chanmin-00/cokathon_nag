package com.example.cokathon.image.dto;

public record ImageDTO(
		S3InfoDTO s3InfoDTO
) {
	public static ImageDTO from(final S3InfoDTO s3InfoDTO) {
		return new ImageDTO(s3InfoDTO);
	}
}
