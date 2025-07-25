package com.example.cokathon.nag.dto.request;

import com.example.cokathon.nag.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "잔소리 생성 요청 DTO")
public record NagCreateRequest(

        @NotNull
        @Schema(description = "카테고리", example = "FOOD")
        Category category,

        @NotBlank
        @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
        String imageUrl,

        @NotBlank
        //@Size(max = 500)
        @Schema(description = "텍스트 내용", example = "하이")
        String text
) {}
