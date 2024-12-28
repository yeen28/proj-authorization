package com.proj.jwt.util;

import com.proj.jwt.model.UserInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
	private final Key key;
	private final long accessTokenExpTime;

	public JwtUtil(
		@Value("${jwt.secret}") final String secret,
		@Value("${jwt.expiration-time}") final long accessTokenExpTime
	) {
		byte[] key = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(key);
		this.accessTokenExpTime = accessTokenExpTime;
	}

	/**
	 * Generate Access Token
	 * @return Access Token String
	 */
	public String generateAccessToken(UserInfoDto userInfo) {
		// Generate JWT
		Claims claims = Jwts.claims();
		claims.put("id", userInfo.getEmail());
		claims.put("role", userInfo.getRole());

		ZonedDateTime now = ZonedDateTime.now();

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(Date.from(now.toInstant()))
				.setExpiration(Date.from(now.plusSeconds(accessTokenExpTime).toInstant()))
				.signWith(key, SignatureAlgorithm.HS512)
				.compact();
	}

	/**
	 * Validate JWT
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token);
			return true;

		} catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token - {}", token, e);
		} catch (
				ExpiredJwtException e) {
			log.info("Expired JWT Token - {}", token, e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token - {}", token, e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty - {}", token, e);
		}

		return false;
	}
}