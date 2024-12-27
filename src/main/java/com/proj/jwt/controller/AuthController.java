package com.proj.jwt.controller;

import com.proj.jwt.model.UserInfoModel;
import com.proj.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@PostMapping("/join")
	public String join(@RequestBody UserInfoModel userInfo) {
		userInfo.setPassword(bCryptPasswordEncoder.encode(userInfo.getPassword()));
		userInfo.setRoles("ROLE_USER");
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
	public String manager() {
		return "<h1>Hi Manager!</h1>";
	}

	// Only "admin" can access
	@GetMapping("/api/admin")
	public String admin() {
		return "<h1>Hi Admin!</h1>";
	}
}