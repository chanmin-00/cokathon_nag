package com.example.cokathon.email.service;

import com.example.cokathon.email.dto.MailHtmlSendDTO;

public interface EmailSender {
	void sendHtmlEmail(MailHtmlSendDTO mailHtmlSendDTO);
}
