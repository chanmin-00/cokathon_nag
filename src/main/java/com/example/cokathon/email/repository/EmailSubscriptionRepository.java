package com.example.cokathon.email.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cokathon.email.domain.EmailSubscription;

public interface EmailSubscriptionRepository extends JpaRepository<EmailSubscription, Long> {

	// 이메일 구독 조회
	Optional<EmailSubscription> findByEmail(String email);

	// 전송 시간 기반 이메일 구독 조회
	List<EmailSubscription> findAllBySendTime(LocalTime sendTime);

	// 이메일 구독 존재 여부 확인
	boolean existsByEmail(String email);

	// 이메일 구독 삭제
	void deleteByEmail(String email);

}
