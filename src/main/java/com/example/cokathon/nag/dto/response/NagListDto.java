package com.example.cokathon.nag.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "리스트 응답 DTO")
public record NagListDto(
        @Schema(description = "카드 id")
        Long id,

        @Schema(description = "내용")
        String text,

        @Schema(description = "작성자 이름")
        String name,

        @Schema(description = "이미지 URL")
        long imageUrl,

        @Schema(description = "얼굴 이미지 URL")
        long faceImageUrl,

        @Schema(description = "좋아요 수")
        int likes,

        @Schema(description = "싫어요 수")
        int dislikes,

        @Schema(description = "신고 수")
        int reposrts,

        @Schema(description = "생성 날짜")
        LocalDateTime createdDate
) {}

