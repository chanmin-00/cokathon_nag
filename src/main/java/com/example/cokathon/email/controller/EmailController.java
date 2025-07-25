package com.example.cokathon.email.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.cokathon.email.dto.request.EmailSubscriptionRequest;
import com.example.cokathon.email.dto.request.EmailUnsubscriptionRequest;
import com.example.cokathon.email.service.EmailSubscriptionService;
import com.example.cokathon.global.dto.DataResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Tag(name = "이메일 구독", description = "이메일 구독 관련 API")
public class EmailController {

	private final EmailSubscriptionService emailSubscriptionService;

	@PostMapping("/subscribe")
	public ResponseEntity<DataResponse<Void>> addEmailSubscription(
		@Valid @RequestBody EmailSubscriptionRequest request) {

		emailSubscriptionService.addEmailSubscription(request);

		return ResponseEntity.ok(DataResponse.ok());
	}

	@DeleteMapping("/unsubscribe")
	public ResponseEntity<DataResponse<Void>> deleteEmailSubscription(
		@Valid @RequestBody EmailUnsubscriptionRequest request) {

		emailSubscriptionService.deleteEmailSubscription(request);

		return ResponseEntity.ok(DataResponse.ok());
	}
}
