package com.example.cokathon.email.dto.request;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.cokathon.news.enums.Category;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailSubscriptionRequest(
	@Email String email,

	@NotNull
	@DateTimeFormat(pattern = "HH:mm")
	@Schema(type = "string", example = "14:00")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
	LocalTime sendTime,

	@NotNull Category category
) {
}
