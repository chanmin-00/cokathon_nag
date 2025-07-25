package com.example.cokathon.email.dto;

public record MailHtmlSendDTO(
	String emailAddr,
	String subject,     // 이메일 제목
	String content,     // 이메일 내용 (잔소리 메시지 등)
	String backgroundImageUrl, // 배경 이미지 URL
	String logoImageUrl  // 로고 이미지 URL
) {
}
