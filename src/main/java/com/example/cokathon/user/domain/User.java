package com.example.cokathon.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

	private User(String email, String password, Role role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}

    public static User toUser(String email, String password) {
		return new User(
			email,
			password,
			Role.ROLE_USER
		);
	}

	public static User toAdminUser(String email, String password) {
		return new User(
			email,
			password,
			Role.ROLE_ADMIN
		);
	}
}
