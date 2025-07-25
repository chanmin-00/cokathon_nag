package com.example.cokathon.nag.dto.request;

import com.example.cokathon.nag.enums.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "잔소리 생성 요청 DTO")
public record NagCreateRequest(

	@NotNull
	@Schema(description = "카테고리 목록", example = "[\"자취\", \"직장\"]")
	List<Category> categories,

	String imageUrl,

	String faceImageUrl,

	@NotBlank
	@Schema(description = "텍스트 내용", example = "하이")
	String text,

	@NotBlank
	@Schema(description = "작성자 이름", example = "홍길동")
	String name
) {
}
