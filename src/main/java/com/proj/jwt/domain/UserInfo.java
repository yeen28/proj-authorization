package com.proj.jwt.domain;

import com.proj.jwt.model.UserInfoModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private String username;
	private String password;
	private String roles;

	public static UserInfo of(UserInfoModel model) {
		return UserInfo.builder()
				.password(model.getPassword())
				.roles(model.getRoles())
				.build();
	}
}