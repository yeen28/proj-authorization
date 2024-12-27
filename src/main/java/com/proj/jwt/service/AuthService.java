package com.proj.jwt.service;

import com.proj.jwt.domain.UserInfo;
import com.proj.jwt.model.UserInfoModel;
import com.proj.jwt.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {
	private final UserInfoRepository userInfoRepository;

	public void save(UserInfoModel model) {
		userInfoRepository.save(UserInfo.of(model));
	}
}