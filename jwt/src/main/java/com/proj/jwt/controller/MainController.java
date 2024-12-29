package com.proj.jwt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	@GetMapping("/")
	public String index() {
		return "<h1>index</h1>";
	}

	@GetMapping("/main")
	public String main() {
		return "<h1>main</h1>";
	}
}
