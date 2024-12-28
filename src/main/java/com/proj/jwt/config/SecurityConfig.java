package com.proj.jwt.config;

import com.proj.jwt.filter.JwtAuthenticationFilter;
import com.proj.jwt.repository.UserInfoRepository;
import com.proj.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtUtil jwtUtil;
	private final UserInfoRepository userInfoRepository;
	private static final String[] AUTH_WHITELIST = {
			"/**"
	};

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// CSRF, CORS
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())

				// Configured with no session management state. Spring Security does not create or use sessions.
				.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Disable formLogin, BasicHttp
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)

				// Do JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
				.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userInfoRepository), UsernamePasswordAuthenticationFilter.class)

				// Handling authentication failure and authorization failure exceptions
				.exceptionHandling(exceptionHandling ->
						exceptionHandling
								.authenticationEntryPoint(new Http403ForbiddenEntryPoint() {
									@Override
									public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
										response.sendRedirect("/api/login");
									}
								})
								.accessDeniedHandler((request, response, accessDeniedException) -> log.warn("Don't have permission."))
				)

				// Permission rules
				.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
						.requestMatchers(AUTH_WHITELIST).permitAll()
						// Allows authentication for all paths because it uses @PreAuthorization
						.anyRequest().permitAll()
				);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}