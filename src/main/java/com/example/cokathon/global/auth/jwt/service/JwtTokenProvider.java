package com.example.cokathon.global.auth.jwt.service;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.example.cokathon.global.auth.dto.JwtTokenResponse;
import com.example.cokathon.global.redis.RedisDao;
import com.example.cokathon.user.exception.UserErrorCode;
import com.example.cokathon.user.exception.UserException;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	private static final String GRANT_TYPE = "Bearer";
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24; // 24시간
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 3; // 3일

	private final Key key;
	private final UserDetailsService userDetailsService;
	private final RedisDao redisDao;

	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
		UserDetailsService userDetailsService,
		RedisDao redisDao) {
		byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.userDetailsService = userDetailsService;
		this.redisDao = redisDao;
	}

	public JwtTokenResponse generateToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		String username = authentication.getName();
		return getJwtToken(authorities, username);
	}

	public JwtTokenResponse generateTokenWithRefreshToken(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		String authorities = userDetails.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		return getJwtToken(authorities, username);
	}

	private JwtTokenResponse getJwtToken(String authorities, String username) {
		long now = System.currentTimeMillis();

		String accessToken = generateAccessToken(username, authorities, new Date(now + ACCESS_TOKEN_EXPIRE_TIME));
		String refreshToken = generateRefreshToken(username, new Date(now + REFRESH_TOKEN_EXPIRE_TIME));

		redisDao.setValues(username, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));
		return JwtTokenResponse.of(GRANT_TYPE, accessToken, refreshToken);
	}

	private String generateAccessToken(String username, String authorities, Date expireDate) {
		return Jwts.builder()
			.setSubject(username)
			.claim("auth", authorities)
			.setExpiration(expireDate)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	private String generateRefreshToken(String username, Date expireDate) {
		return Jwts.builder()
			.setSubject(username)
			.setExpiration(expireDate)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);
		if (claims.get("auth") == null) {
			throw UserException.from(UserErrorCode.UNAUTHORIZED_ACCESS);
		}

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get("auth").toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.toList();

		UserDetails principal = CustomUserDetails.of(
			Long.parseLong(claims.getSubject()), authorities
		);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build()
				.parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		} catch (JwtException e) {
			throw UserException.from(UserErrorCode.INVALID_TOKEN);
		}
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (JwtException e) {
			log.warn("JWT Token Error: {}", e.getMessage());
			throw UserException.from(UserErrorCode.INVALID_TOKEN);
		}
	}

	public boolean validateRefreshToken(String token) {
		if (!validateToken(token))
			return false;

		try {
			String username = getUserNameFromToken(token);
			String storedToken = (String)redisDao.getValues(username);
			return token.equals(storedToken);
		} catch (Exception e) {
			throw UserException.from(UserErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	public String getUserNameFromToken(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build()
				.parseClaimsJws(token).getBody().getSubject();
		} catch (ExpiredJwtException e) {
			return e.getClaims().getSubject();
		} catch (JwtException e) {
			throw UserException.from(UserErrorCode.INVALID_TOKEN);
		}
	}

	public void deleteRefreshToken(String username) {
		if (username == null || username.isBlank()) {
			throw UserException.from(UserErrorCode.INVALID_REFRESH_TOKEN);
		}
		redisDao.deleteValues(username);
	}
}
