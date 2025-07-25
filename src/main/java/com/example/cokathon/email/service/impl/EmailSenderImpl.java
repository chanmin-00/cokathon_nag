package com.example.cokathon.email.service.impl;

import static com.example.cokathon.email.exception.EmailSubscriptionErrorCode.*;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
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
import com.example.cokathon.nag.domain.Nag;
import com.example.cokathon.nag.enums.Category;
import com.example.cokathon.nag.repository.NagRepository;

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
	private final NagRepository nagRepository;

	@Override
	@Scheduled(cron = "0 * * * * *") // 매 분 실행
	public void sendScheduledEmails() {
		LocalTime now = LocalTime.now().withSecond(0).withNano(0); // 초 제거

		List<EmailSubscription> subscribers = emailSubscriptionRepository.findAllBySendTime(now);

		for (EmailSubscription sub : subscribers) {
			try {
				Nag nag = pickRandomNag(sub.getCategory());
				if (nag == null) {
					log.info("해당 카테고리 [{}]에 잔소리가 없어 전송 생략: {}", sub.getCategory(), sub.getEmail());
					continue;
				}

				MailHtmlSendDTO dto = MailHtmlSendDTO.of(
					sub.getEmail(),
					"[툭] " + sub.getCategory().name() + "에 대한 한마디, 오늘 당신에게",
					nag.getText(),
					sub.getCategory().name(),
					nag.getName(),
					nag.getImageUrl(),
					"https://ttok.today",
					"https://ttok.today/unsubscribe?email=" + sub.getEmail()
				);

				sendHtmlEmail(dto);

			} catch (Exception e) {
				log.error("이메일 전송 실패: {}", sub.getEmail(), e);
			}
		}
	}

	@Override
	public void sendHtmlEmail(MailHtmlSendDTO dto) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(dto.emailAddr());
			helper.setSubject(dto.subject());

			// Thymeleaf 컨텍스트 설정
			Context context = new Context();
			context.setVariable("nagText", dto.nagText());
			context.setVariable("categoryName", dto.categoryName());
			context.setVariable("author", dto.author());
			context.setVariable("nagImageUrl", dto.nagImageUrl());
			context.setVariable("mainLink", dto.mainLink());
			context.setVariable("unsubscribeLink", dto.unsubscribeLink());

			// HTML 템플릿 처리
			String htmlContent = templateEngine.process("email-template", context);
			helper.setText(htmlContent, true); // true = HTML

			javaMailSender.send(message);

			log.info("이메일 전송 성공: {}", dto.emailAddr());

		} catch (MessagingException e) {
			log.error("이메일 전송 실패: {}", dto.emailAddr(), e);
			throw EmailSubscriptionException.from(SEND_EMAIL_ERROR);
		}
	}

	// 카테고리에 해당하는 잔소리 메시지를 랜덤으로 선택
	private Nag pickRandomNag(Category category) {
		List<Nag> nagList = nagRepository.findRandomByCategory(category, PageRequest.of(0, 1));
		return nagList.isEmpty() ? null : nagList.get(0);
	}
}
