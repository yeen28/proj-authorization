package com.proj.jwt.domain;

import com.proj.jwt.model.UserInfoDto;
import com.proj.jwt.type.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String email;
	private String password;
	@Enumerated(EnumType.STRING)
	private RoleType role;

	public static UserInfo of(UserInfoDto userInfoDto) {
		return UserInfo.builder()
				.email(userInfoDto.getEmail())
				.password(userInfoDto.getPassword())
				.role(userInfoDto.getRole())
				.build();
	}
}