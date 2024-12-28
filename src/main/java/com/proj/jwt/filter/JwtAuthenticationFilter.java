package com.proj.jwt.filter;

import com.proj.jwt.model.CustomUserDetails;
import com.proj.jwt.model.UserInfoDto;
import com.proj.jwt.repository.UserInfoRepository;
import com.proj.jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { // OncePerRequestFilter : One-time execution guarantee
	private final JwtUtil jwtUtil;
	private final UserInfoRepository userInfoRepository;

	/**
	 * JWT Validation Filter
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeader = request.getHeader("Authorization");

		// if JWT is in the Header
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7);
			String email = "email";

			// Validate JWT
			if (jwtUtil.validateToken(token)) {
				UserInfoDto userInfoDto = UserInfoDto.of(userInfoRepository.findByEmail(email));

				// if user matches token, generate userDetails.
				UserDetails userDetails = new CustomUserDetails(userInfoDto);

				// UserDetails, password, role -> Generate access authorization tokens
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				// Set authorizations to the “security context” of “request”
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				log.info("Saved credentials in Security Context.");
			}
		}

		// Do Next Filter
		filterChain.doFilter(request, response);
	}
}