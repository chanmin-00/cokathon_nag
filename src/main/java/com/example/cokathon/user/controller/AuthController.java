package com.example.cokathon.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cokathon.global.auth.dto.JwtTokenResponse;
import com.example.cokathon.global.dto.DataResponse;
import com.example.cokathon.user.dto.request.LoginRequest;
import com.example.cokathon.user.dto.request.SignUpRequest;
import com.example.cokathon.user.dto.request.TokenReissueRequest;
import com.example.cokathon.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

	@PostMapping("/signup")
	public ResponseEntity<DataResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {

		userService.createUser(request.email(), request.password());
		return ResponseEntity.ok(DataResponse.ok());
	}

	@PostMapping("/login")
	public ResponseEntity<DataResponse<JwtTokenResponse>> login(@Valid @RequestBody LoginRequest request) {

		JwtTokenResponse jwtToken = userService.login(request.email(), request.password());
		return ResponseEntity.ok(DataResponse.from(jwtToken));
	}

	@PostMapping("/reissue")
	public ResponseEntity<DataResponse<JwtTokenResponse>> reissue(@Valid @RequestBody TokenReissueRequest request) {

		JwtTokenResponse jwtToken = userService.reissue(request.refreshToken());
		return ResponseEntity.ok(DataResponse.from(jwtToken));
	}

	@DeleteMapping("/user")
	public ResponseEntity<DataResponse<Void>> deleteUser(@Valid @RequestBody TokenReissueRequest request) {
		return ResponseEntity.ok(DataResponse.ok());
	}

}
