package com.example.cokathon.email.domain;

import java.time.LocalTime;

import com.example.cokathon.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class EmailSubscription extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private LocalTime sendTime;

	private EmailSubscription(String email, LocalTime sendTime) {
		this.email = email;
		this.sendTime = sendTime;
	}

	public static EmailSubscription of(final String email, final LocalTime sendTime) {
		return new EmailSubscription(
			email,
			sendTime
		);
	}

	public void updateEmailSubscription(String email, LocalTime sendTime) {
		this.email = email;
		this.sendTime = sendTime;
	}
}
