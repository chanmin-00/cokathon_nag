package com.example.cokathon.global.auth.dto;

import lombok.Getter;

@Getter
public class JwtTokenResponse {

	private final String grantType; // JWT 토큰 타입 (Bearer)
	private final String accessToken;
	private final String refreshToken;

	private JwtTokenResponse(String grantType, String accessToken, String refreshToken) {
		this.grantType = grantType;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public static JwtTokenResponse of(String grantType, String accessToken, String refreshToken) {
		return new JwtTokenResponse(grantType, accessToken, refreshToken);
	}
}
