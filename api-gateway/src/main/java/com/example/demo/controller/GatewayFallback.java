package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class GatewayFallback {

	@GetMapping("/testService")
	public FallbackResponse getFallBackBackendA() {
		FallbackResponse a = new FallbackResponse();
		a.setMsgCode(500);
		a.setMsg("fallBackMsg");
		return a;
	}

	@GetMapping("/testInternalServiceError")
	public FallbackResponse getFallBackBackendB() {
		FallbackResponse a = new FallbackResponse();
		a.setMsgCode(500);
		a.setMsg("fallBackMsg");
		return a;
	}
	
	@GetMapping("/sample")
	public String sampleError() {
		System.err.println("sample called");
		return String.valueOf(1/0);
	}
	
}