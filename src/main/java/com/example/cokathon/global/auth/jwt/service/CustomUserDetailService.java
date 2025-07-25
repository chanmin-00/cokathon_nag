package com.example.cokathon.global.auth.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.cokathon.user.domain.User;
import com.example.cokathon.user.exception.UserErrorCode;
import com.example.cokathon.user.exception.UserException;
import com.example.cokathon.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	// pk 받아서 해당 유저를 찾아 CustomUserDetails로 반환
	@Override
	public UserDetails loadUserByUsername(String id) {
		User user = userRepository.findById(Long.parseLong(id))
			.orElseThrow(() -> UserException.from(UserErrorCode.USER_NOT_FOUND));

		return CustomUserDetails.of(
			user.getId(),
			user.getRole().name()
		);
	}
}
