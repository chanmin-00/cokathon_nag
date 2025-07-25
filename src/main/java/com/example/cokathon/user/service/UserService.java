package com.example.cokathon.user.service;

import com.example.cokathon.global.auth.dto.JwtTokenResponse;

public interface UserService {

	void createUser(String email, String password);

	JwtTokenResponse login(String email, String password);

	JwtTokenResponse reissue(String refreshToken);
}
