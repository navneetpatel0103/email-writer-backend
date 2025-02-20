package com.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.email.responseObject.EmailRequest;
import com.email.service.EmailService;

@CrossOrigin(origins = "https://mail.google.com")
@RestController
@RequestMapping("/api/mail")
public class EmailController {
	
	@Autowired
	private EmailService emailService;
	
	@PostMapping("/generateMailReply")
	public String generateEmailReply(@RequestBody EmailRequest emailRequest) {
		try {
			String response = emailService.generateEmailReply(emailRequest);
			return response;
		}catch (Exception e) {
			System.out.println("Error : "+ e.getMessage());
		}
		return "";
		
	}

}
