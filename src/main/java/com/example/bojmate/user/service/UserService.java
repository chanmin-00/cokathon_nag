package com.example.bojmate.user.service;

import com.example.bojmate.global.auth.dto.JwtTokenResponse;

public interface UserService {

	void createUser(String email, String password);

	JwtTokenResponse login(String email, String password);

	JwtTokenResponse reissue(String refreshToken);
}
