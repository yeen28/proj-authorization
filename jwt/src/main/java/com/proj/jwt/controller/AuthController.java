package com.proj.jwt.controller;

import com.proj.jwt.model.UserInfoDto;
import com.proj.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/api/login")
	public ResponseEntity<String> login(@RequestBody UserInfoDto userInfoDto) {
		String token = authService.login(userInfoDto);
		return ResponseEntity.status(HttpStatus.OK).body(token);
	}

	@PostMapping("/join")
	public String join(@RequestBody UserInfoDto userInfo) {
		authService.save(userInfo);
		return "<h1>Sign up complete</h1>";
	}

	// "user", "manager", "admin" can access
	@GetMapping("/api/user")
	public String user() {
		return "<h1>Hi User!</h1>";
	}

	// Only "manager" and "admin" can access
	@GetMapping("/api/manager")
	@PreAuthorize("hasRole('ROLE_MANAGER')")
	public String manager() {
		return "<h1>Hi Manager!</h1>";
	}

	// Only "admin" can access
	@GetMapping("/api/admin")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String admin() {
		return "<h1>Hi Admin!</h1>";
	}
}