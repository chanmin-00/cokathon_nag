package com.example.cokathon.email.service;


import com.example.cokathon.email.dto.request.EmailSubscriptionRequest;
import com.example.cokathon.email.dto.request.EmailUnsubscriptionRequest;

public interface EmailSubscriptionService {

	void addEmailSubscription(EmailSubscriptionRequest request);

	void deleteEmailSubscription(EmailUnsubscriptionRequest request);
}
