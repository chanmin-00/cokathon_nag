package com.example.cokathon.user.service.impl;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cokathon.global.auth.jwt.service.JwtTokenProvider;
import com.example.cokathon.global.auth.dto.JwtTokenResponse;
import com.example.cokathon.user.domain.User;
import com.example.cokathon.user.exception.UserErrorCode;
import com.example.cokathon.user.exception.UserException;
import com.example.cokathon.user.repository.UserRepository;
import com.example.cokathon.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	@Transactional
	public void createUser(String email, String password) {
		userRepository.findByEmail(email)
			.ifPresent(user -> {
				throw UserException.from(UserErrorCode.USER_ALREADY_EXISTS);
			});

		String encodedPassword = passwordEncoder.encode(password);

		userRepository.save(User.toUser(email, encodedPassword));

	}

	@Override
	public JwtTokenResponse login(String email, String password) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> UserException.from(UserErrorCode.USER_NOT_FOUND));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw UserException.from(UserErrorCode.INVALID_PASSWORD);
		}

		// 인증 객체 생성
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			user.getId().toString(), null, List.of(new SimpleGrantedAuthority(user.getRole().name()))
		);

		return jwtTokenProvider.generateToken(authentication);
	}

	@Override
	public JwtTokenResponse reissue(String refreshToken) {
		if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
			throw UserException.from(UserErrorCode.INVALID_REFRESH_TOKEN);
		}

		String username = jwtTokenProvider.getUserNameFromToken(refreshToken);
		return jwtTokenProvider.generateTokenWithRefreshToken(username);
	}
}

