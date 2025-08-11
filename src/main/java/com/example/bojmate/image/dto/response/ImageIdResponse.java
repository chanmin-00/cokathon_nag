package com.example.bojmate.image.dto.response;

public record ImageIdResponse(
		Long imageId
) {
	public static ImageIdResponse from(final Long imageId) {
		return new ImageIdResponse(imageId);
	}
}
