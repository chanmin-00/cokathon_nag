package com.example.cokathon.email.dto;

public record MailHtmlSendDTO(
	String emailAddr,
	String subject,
	String nagText,
	String categoryName,
	String author,
	String nagImageUrl,      // 배경 이미지
	String faceImageUrl,      // 얼굴 이미지
	String mainLink,
	String unsubscribeLink
) {
	public static MailHtmlSendDTO of(
		String emailAddr,
		String subject,
		String nagText,
		String categoryName,
		String author,
		String nagImageUrl,
		String faceImageUrl,
		String mainLink,
		String unsubscribeLink
	) {
		return new MailHtmlSendDTO(
			emailAddr,
			subject,
			nagText,
			categoryName,
			author,
			nagImageUrl,
			faceImageUrl,
			mainLink,
			unsubscribeLink
		);
	}
}