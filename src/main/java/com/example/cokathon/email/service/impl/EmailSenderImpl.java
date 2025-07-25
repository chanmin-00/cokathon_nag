package com.example.cokathon.email.service.impl;

import static com.example.cokathon.email.exception.EmailSubscriptionErrorCode.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import com.example.cokathon.email.domain.EmailSubscription;
import com.example.cokathon.email.dto.MailHtmlSendDTO;
import com.example.cokathon.email.exception.EmailSubscriptionException;
import com.example.cokathon.email.repository.EmailSubscriptionRepository;
import com.example.cokathon.email.service.EmailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;

	private final EmailSubscriptionRepository emailSubscriptionRepository;

	@Scheduled(cron = "0 * * * * *") // 매 분 실행
	public void sendScheduledEmails() {
		LocalTime now = LocalTime.now().withSecond(0).withNano(0); // 초 제거

		List<EmailSubscription> subscribers = emailSubscriptionRepository.findAllBySendTime(now);

		for (EmailSubscription sub : subscribers) {
			try {
				// 카테고리 별 잔소리 메시지 선택 후 전송
			} catch (Exception e) {
				log.error("이메일 전송 실패: {}", sub.getEmail(), e);
			}
		}

	}

	public void sendHtmlEmail(MailHtmlSendDTO mailHtmlSendDTO) {
		MimeMessage message = javaMailSender.createMimeMessage();
		String backgroundImadgeUrl = mailHtmlSendDTO.backgroundImageUrl();
		String logoImageUrl = mailHtmlSendDTO.logoImageUrl();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(mailHtmlSendDTO.emailAddr());
			helper.setSubject(mailHtmlSendDTO.subject());

			Context context = new Context();
			context.setVariable("date",
				LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")) + " · 잔소리함");
			context.setVariable("title", "오늘도 당신에게 전하는 한마디");
			context.setVariable("message", mailHtmlSendDTO.content());

			String htmlContent = templateEngine.process("email-template", context);
			helper.setText(htmlContent, true);

			helper.addInline("bgImage", new ClassPathResource(backgroundImadgeUrl));
			helper.addInline("logoImage", new ClassPathResource(logoImageUrl));

			javaMailSender.send(message);

		} catch (MessagingException e) {
			throw EmailSubscriptionException.from(SEND_EMAIL_ERROR);
		}
	}

	// 카테고리에 해당하는 잔소리 메시지를 랜덤으로 선택l
	private String pickRandomMessage(String category) {
		return "랜덤 메시지"; // TODO: 실제 메시지 선택 로직 구현
	}
}
