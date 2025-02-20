package com.email.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.email.responseObject.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

@Service
public class EmailService {

	private final WebClient webClient;

	@Value("${gemini.api.url}")
	private String geminiApiUrl;

	@Value("${gemini.api.key}")
	private String geminiApiKey;

	public EmailService(WebClient.Builder webclient) {
		this.webClient = webclient.build();
	}

	public String generateEmailReply(EmailRequest emailRequest) {
		String prompt = buildPrompt(emailRequest);
		// casting the request to sent to AI
		Map<String, Object> requestBody = Map.of("contents",
				new Object[] { Map.of("parts", new Object[] { Map.of("text", prompt) }) });

		// do request and get response
		String response = webClient.post()
				.uri(geminiApiUrl + geminiApiKey)
				.header("Content-Type", "application/json")
				.bodyValue(requestBody).retrieve()
				.onStatus(status -> status.is4xxClientError(),
						res -> Mono.error(new RuntimeException("Client Error! Check your request")))
				.onStatus(status -> status.is5xxServerError(),
						res -> Mono.error(new RuntimeException("Server error! Try again later.")))
				.bodyToMono(String.class)
				.block();
		
		// extract response and return 
		return extractResponseContent(response);
	}

	private String extractResponseContent(String response) {

		String finalResponse = null;

		try {

			ObjectMapper objectMapper = new ObjectMapper();

			JsonNode rootNode = objectMapper.readTree(response);

			JsonNode candidatesNode = rootNode.path("candidates");

			if (candidatesNode.isArray() && candidatesNode.size() > 0) {
				JsonNode firstCandidate = candidatesNode.get(0);

				JsonNode contentNode = firstCandidate.path("content");

				JsonNode partsNode = contentNode.path("parts");

				if (partsNode.isArray() && partsNode.size() > 0) {
					JsonNode firstPart = partsNode.get(0);

					finalResponse = firstPart.path("text").asText();
				}
			}

		} catch (Exception e) {
			return "Error in fetching the response : " + e.getMessage();
		}
		return finalResponse;
	}

	private String buildPrompt(EmailRequest emailRequest) {
		StringBuilder prompt = new StringBuilder();
		prompt.append(
				"Generate a professional reply for the below email content. Please do not generate a subject line or anything else.");
		if (StringUtils.isNotBlank(emailRequest.getTone())) {
			prompt.append("Use a ").append(emailRequest.getTone()).append(" tone");
		}
		prompt.append("\n Original Email : \n").append(emailRequest.getEmailContent());
		return prompt.toString();
	}

}

/*
 * Alternate approach to cast a request to send to AI
 import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

ObjectMapper objectMapper = new ObjectMapper();

Map<String, Object> partMap = new HashMap<>();
partMap.put("text", prompt);

Map<String, Object> partsMap = new HashMap<>();
partsMap.put("parts", new Object[]{ partMap });

Map<String, Object> requestMap = new HashMap<>();
requestMap.put("contents", new Object[]{ partsMap });

String requestBodyJson = objectMapper.writeValueAsString(requestMap);

 */
