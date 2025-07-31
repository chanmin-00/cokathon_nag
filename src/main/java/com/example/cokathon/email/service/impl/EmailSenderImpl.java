package com.example.cokathon.email.service.impl;

import static com.example.cokathon.email.exception.EmailSubscriptionErrorCode.SEND_EMAIL_ERROR;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.cokathon.email.domain.EmailSubscription;
import com.example.cokathon.email.dto.MailHtmlSendDTO;
import com.example.cokathon.email.exception.EmailSubscriptionException;
import com.example.cokathon.email.repository.EmailSubscriptionRepository;
import com.example.cokathon.email.service.EmailSender;
import com.example.cokathon.news.domain.Nag;
import com.example.cokathon.news.enums.Category;
import com.example.cokathon.news.repository.NagRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderImpl implements EmailSender {

	private static final String MAIN_LINK = "https://ttok.today";
	private static final String DEFAULT_IMAGE = "https://cokathon.s3.ap-northeast-2.amazonaws.com/frame5-1.png";
	private static final String DEFAULT_FACE_IMAGE = "https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-03.png";

	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;
	private final EmailSubscriptionRepository emailSubscriptionRepository;
	private final NagRepository nagRepository;

	private static final Map<Integer, String> NAG_IMAGE_URLS = Map.ofEntries(
		Map.entry(1, "https://cokathon.s3.ap-northeast-2.amazonaws.com/frame5-1.png"),
		Map.entry(2, "https://cokathon.s3.ap-northeast-2.amazonaws.com/frame3-1.png"),
		Map.entry(3, "https://cokathon.s3.ap-northeast-2.amazonaws.com/frame4-1.png"),
		Map.entry(4, "https://cokathon.s3.ap-northeast-2.amazonaws.com/frame1-1.png"),
		Map.entry(5, "https://cokathon.s3.ap-northeast-2.amazonaws.com/frame2-1.png"),
		Map.entry(6, "https://cokathon.s3.ap-northeast-2.amazonaws.com/frame7-1.png")
	);

	private static final Map<Integer, String> FACE_IMAGE_URLS = Map.ofEntries(
		Map.entry(1,
			"https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-03.png"),
		Map.entry(2,
			"https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-04.png"),
		Map.entry(3,
			"https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-05.png"),
		Map.entry(4,
			"https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-06.png"),
		Map.entry(5,
			"https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-07.png"),
		Map.entry(6,
			"https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-08.png"),
		Map.entry(7,
			"https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-09.png"),
		Map.entry(8,
			"https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-10.png"),
		Map.entry(9,
			"https://cokathon.s3.ap-northeast-2.amazonaws.com/%E1%84%90%E1%85%AE%E1%86%A8+%E1%84%8B%E1%85%B5%E1%84%86%E1%85%A9%E1%84%90%E1%85%B5%E1%84%8F%E1%85%A9%E1%86%AB-11.png")
	);

	private String getTextColorByNagImageKey(String key) {
		if (key == null) return "#FFFFFF";
		try {
			int value = Integer.parseInt(key);
			return (value == 1 || value == 5 || value == 7) ? "#000000" : "#FFFFFF";
		} catch (NumberFormatException e) {
			return "#FFFFFF";
		}
	}


	@Override
	@Scheduled(cron = "0 * * * * *") // 매 분 실행
	public void sendScheduledEmails() {
		LocalTime now = LocalTime.now().withSecond(0).withNano(0);
		List<EmailSubscription> subscribers = emailSubscriptionRepository.findAllBySendTime(now);

		for (EmailSubscription sub : subscribers) {
			try {
				Nag nag = pickRandomNag(sub.getCategory());
				if (nag == null) {
					log.info("해당 카테고리 [{}]에 잔소리가 없어 전송 생략: {}", sub.getCategory(), sub.getEmail());
					continue;
				}

				String nagImageUrl = getImageUrl(NAG_IMAGE_URLS, nag.getImageUrl(), DEFAULT_IMAGE);
				String faceImageUrl = getImageUrl(FACE_IMAGE_URLS, nag.getFaceImageUrl(), DEFAULT_FACE_IMAGE);
				String textColor = getTextColorByNagImageKey(nag.getImageUrl());

				MailHtmlSendDTO dto = buildMailDto(sub, nag, nagImageUrl, faceImageUrl, textColor);
				sendHtmlEmail(dto);

			} catch (Exception e) {
				log.error("이메일 전송 실패: {}", sub.getEmail(), e);
			}
		}
	}

	private String getImageUrl(Map<Integer, String> map, String key, String defaultUrl) {
		Number intValue = key != null ? Integer.parseInt(key) : null;

		return map.getOrDefault(intValue, defaultUrl);
	}

	private MailHtmlSendDTO buildMailDto(EmailSubscription sub, Nag nag, String nagImageUrl, String faceImageUrl, String textColor) {
		return MailHtmlSendDTO.of(
			sub.getEmail(),
			"[툭] " + sub.getCategory().name() + "에 대한 한마디, 오늘 당신에게",
			nag.getText(),
			sub.getCategory().name(),
			nag.getName(),
			nagImageUrl,
			faceImageUrl,
			textColor,
			MAIN_LINK,
			MAIN_LINK + "/unsubscribe?email=" + sub.getEmail()
		);
	}

	@Override
	public void sendHtmlEmail(MailHtmlSendDTO dto) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(dto.emailAddr());
			helper.setSubject(dto.subject());

			Context context = new Context();
			context.setVariable("nagText", dto.nagText());
			context.setVariable("categoryName", dto.categoryName());
			context.setVariable("author", dto.author());
			context.setVariable("nagImageUrl", dto.nagImageUrl());
			context.setVariable("faceImageUrl", dto.faceImageUrl());
			context.setVariable("mainLink", dto.mainLink());
			context.setVariable("unsubscribeLink", dto.unsubscribeLink());
			context.setVariable("textColor", dto.textColor());

			String htmlContent = templateEngine.process("email-template", context);
			helper.setText(htmlContent, true);

			javaMailSender.send(message);
			log.info("이메일 전송 성공: {}", dto.emailAddr());

		} catch (MessagingException e) {
			log.error("이메일 전송 실패: {}", dto.emailAddr(), e);
			throw EmailSubscriptionException.from(SEND_EMAIL_ERROR);
		}
	}

	private Nag pickRandomNag(Category category) {
		List<Nag> nagList = nagRepository.findRandomByCategory(category.name(), PageRequest.of(0, 1));
		return nagList.isEmpty() ? null : nagList.get(0);
	}
}
