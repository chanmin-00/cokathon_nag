package com.example.cokathon.news.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReportResponseDto(
        @Schema(description = "현재 신고 횟수")
        int reports
) {}

