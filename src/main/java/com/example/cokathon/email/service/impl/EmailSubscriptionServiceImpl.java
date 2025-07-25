package com.example.cokathon.email.service.impl;

import static com.example.cokathon.email.exception.EmailSubscriptionErrorCode.*;

import java.time.LocalTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cokathon.email.domain.EmailSubscription;
import com.example.cokathon.email.dto.request.EmailSubscriptionRequest;
import com.example.cokathon.email.dto.request.EmailUnsubscriptionRequest;
import com.example.cokathon.email.exception.EmailSubscriptionException;
import com.example.cokathon.email.repository.EmailSubscriptionRepository;
import com.example.cokathon.email.service.EmailSubscriptionService;
import com.example.cokathon.nag.enums.Category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSubscriptionServiceImpl implements EmailSubscriptionService {

	private final EmailSubscriptionRepository emailSubscriptionRepository;

	// 이메일 구독 추가
	@Override
	@Transactional
	public void addEmailSubscription(EmailSubscriptionRequest request) {
		String email = request.email();
		LocalTime sendTime = request.sendTime();
		Category category = request.category();

		EmailSubscription emailSubscription = emailSubscriptionRepository.findByEmail(request.email())
			.map(existingSubscription -> {
				// 이미 구독 중인 경우 업데이트
				existingSubscription.updateEmailSubscription(email, sendTime, category);
				return existingSubscription;
			})
			.orElseGet(() -> {
				// 구독이 없는 경우 새로 생성
				return EmailSubscription.of(email, sendTime, category);
			});

		emailSubscriptionRepository.save(emailSubscription);
	}

	@Override
	@Transactional
	public void deleteEmailSubscription(EmailUnsubscriptionRequest request) {
		String email = request.email();
		if (emailSubscriptionRepository.existsByEmail(email)) {
			emailSubscriptionRepository.deleteByEmail(email);
		} else {
			throw EmailSubscriptionException.from(SUBSCRIPTION_NOT_FOUND);
		}
	}

}
