package com.example.demo;

import java.util.function.Predicate;
import org.springframework.web.server.ResponseStatusException;


public class HttpInternalServicePredicate implements Predicate<ResponseStatusException> {
	@Override
	public boolean test(ResponseStatusException e) {
		return e.getStatus().is5xxServerError();
	}
}
