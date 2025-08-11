package com.example.bojmate.image.dto.response;

import java.util.List;

public record ImageUrlListResponse(
	List<String> imageUrls
) {
	public static ImageUrlListResponse from(List<String> imageUrls) {
		return new ImageUrlListResponse(imageUrls);
	}
}
