package com.example.cokathon.email.dto.request;

import jakarta.validation.constraints.Email;

public record EmailUnsubscriptionRequest(
	@Email String email
) {
}
