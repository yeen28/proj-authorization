# JWT (Json Web Token)

## What is [JWT](https://jwt.io/introduction)?
- JSON Web Token (JWT) is an open standard (RFC 7519) that defines a compact and self-contained way for securely transmitting information between parties as a JSON object.
- [More...](https://y-e-un28.tistory.com/556#What-is-JWT)

<br/>

## Diagram
![diagram.png](image%2Fdiagram.png)

<br/>

## Directory structure
```
com.xxx.xxx --- config
                 |-------SecurityConfig
            --- domain
                 |-------UserInfo
            --- filter
                 |-------JwtAuthenticationFilter
            --- model
                 |-------CustomUserDetails
                 |-------UserInfoDto
            --- repository
                 |-------UserInfoRepository
            --- controller
                 |-------AuthController
            --- service
                 |-------AuthService
            --- type
                 |-------RoleType
            --- util
                 |--------JwtUtil
```

<br/>

## Call Sequence
1. JwtUtil
   ```java
   public JwtUtil(
      @Value("${jwt.secret}") final String secret,
      @Value("${jwt.expiration-time}") final long accessTokenExpTime
   ) {
      byte[] key = Decoders.BASE64.decode(secret);
      this.key = Keys.hmacShaKeyFor(key);
      this.accessTokenExpTime = accessTokenExpTime;
   }
   ```

2. SecurityConfig
   ```java
   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
   ```

3. SecurityConfig
   ```java
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
   ```

### Execution Flow When Calling the API
1. JwtAuthenticationFilter
   ```java
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
   ```

2. LoginController
