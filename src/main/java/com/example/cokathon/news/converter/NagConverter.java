package com.example.cokathon.news.converter;

import com.example.cokathon.news.domain.Nag;
import com.example.cokathon.news.dto.request.NagCreateRequest;
import com.example.cokathon.news.dto.response.NagListDto;

public class NagConverter {

	private NagConverter() {
	}

	public static NagListDto toNagListDto(Nag nag) {
		return NagListDto.builder()
			.id(nag.getId())
			.text(nag.getText())
			.name(nag.getName())
			.imageUrl(nag.getImageUrl())
			.faceImageUrl(nag.getFaceImageUrl())
			.likes(nag.getLikes())
			.dislikes(nag.getDislikes())
			.reposrts(nag.getReports())
			.createdDate(nag.getCreatedDate())
			.build();
	}

	public static Nag toNag(NagCreateRequest request) {
		return Nag.builder()
			.categories(request.categories())
			.imageUrl(request.imageUrl())
			.faceImageUrl(request.faceImageUrl())
			.text(request.text())
			.name(request.name())
			.likes(0)
			.dislikes(0)
			.reports(0)
			.build();
	}
}
