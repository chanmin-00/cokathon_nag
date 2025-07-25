package com.example.cokathon.global.auth.jwt.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

	private final String username;
	private final Collection<? extends GrantedAuthority> authorities;

	private CustomUserDetails(String username, Collection<? extends GrantedAuthority> authorities) {
		this.username = username;
		this.authorities = authorities;
	}

	public static CustomUserDetails of(Long userId, String roleName) {
		return new CustomUserDetails(
			userId.toString(),
			List.of(new SimpleGrantedAuthority(roleName))
		);
	}

	public static CustomUserDetails of(Long userId, Collection<? extends GrantedAuthority> authorities) {
		return new CustomUserDetails(userId.toString(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return ""; // 비밀번호는 사용하지 않음
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
