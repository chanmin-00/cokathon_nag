package com.example.cokathon.email.domain;

import java.time.LocalTime;

import com.example.cokathon.global.entity.BaseEntity;
import com.example.cokathon.news.enums.Category;

import jakarta.persistence.Column;
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

@Table(name = "email_subscription")
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;

	private EmailSubscription(String email, LocalTime sendTime, Category category) {
		this.email = email;
		this.sendTime = sendTime;
		this.category = category;
	}

	public static EmailSubscription of(final String email, final LocalTime sendTime, final Category category) {
		return new EmailSubscription(
			email,
			sendTime,
			category
		);
	}

	public void updateEmailSubscription(String email, LocalTime sendTime, Category category) {
		this.email = email;
		this.sendTime = sendTime;
		this.category = category;
	}
}
