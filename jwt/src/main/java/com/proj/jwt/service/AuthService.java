package com.proj.jwt.service;

import com.proj.jwt.domain.UserInfo;
import com.proj.jwt.model.UserInfoDto;
import com.proj.jwt.repository.UserInfoRepository;
import com.proj.jwt.type.RoleType;
import com.proj.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
	private final UserInfoRepository userInfoRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public String login(UserInfoDto userInfoDto) {
		String email = userInfoDto.getEmail();
		String password = userInfoDto.getPassword();

		UserInfo userInfo = userInfoRepository.findByEmail(email);
		if (userInfo == null) {
			throw new UsernameNotFoundException("Not Found User");
		}

		if (!passwordEncoder.matches(password, userInfo.getPassword())) {
			throw new BadCredentialsException("Not Matched Password");
		}

		return jwtUtil.generateAccessToken(userInfoDto);
	}

	public void save(UserInfoDto userInfoDto) {
		userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
		userInfoDto.setRole(RoleType.ROLE_USER);
		userInfoRepository.save(UserInfo.of(userInfoDto));
	}
}