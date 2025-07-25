package com.example.cokathon.email.dto.request;

import java.time.LocalTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailSubscriptionRequest(
	@Email String email,
	@NotNull LocalTime sendTime
) {
}
