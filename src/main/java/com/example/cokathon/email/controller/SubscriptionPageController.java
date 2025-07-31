package com.example.cokathon.email.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SubscriptionPageController {

    @GetMapping("/subscription")
    public String subscriptionPage() {
        return "forward:/subscription.html";
    }
    
    @GetMapping("/news/{id}")
    public String newsDetailPage(@PathVariable String id) {
        return "forward:/news-detail.html";
    }
    
    @GetMapping("/standings")
    public String standingsPage() {
        return "forward:/standings.html";
    }
}