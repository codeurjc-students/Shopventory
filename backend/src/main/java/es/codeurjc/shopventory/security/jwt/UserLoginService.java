package es.codeurjc.shopventory.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserLoginService {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private JwtCookieManager cookieUtil;

	public ResponseEntity<AuthResponse> login(LoginRequest loginRequest, String encryptedAccessToken, String 
			encryptedRefreshToken) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String username = loginRequest.getUsername();
		UserDetails user = userDetailsService.loadUserByUsername(username);

		HttpHeaders responseHeaders = new HttpHeaders();
		addAccessTokenCookie(responseHeaders, jwtTokenProvider.generateToken(user));
		addRefreshTokenCookie(responseHeaders, jwtTokenProvider.generateRefreshToken(user));

		AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
				"Auth successful. Tokens are created in cookie.");
		return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
	}

	public ResponseEntity<AuthResponse> refresh(String encryptedRefreshToken) {
		
		String refreshToken = SecurityCipher.decrypt(encryptedRefreshToken);
		
		Boolean refreshTokenValid = jwtTokenProvider.validateToken(refreshToken);
		
		if (!refreshTokenValid) {
			AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.FAILURE,
					"Invalid refresh token !");
			return ResponseEntity.ok().body(loginResponse);
		}

		String username = jwtTokenProvider.getUsername(refreshToken);
		UserDetails user = userDetailsService.loadUserByUsername(username);
				
		Token newAccessToken = jwtTokenProvider.generateToken(user);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil
				.createAccessTokenCookie(newAccessToken.getTokenValue(), newAccessToken.getDuration()).toString());

		AuthResponse loginResponse = new AuthResponse(AuthResponse.Status.SUCCESS,
				"Auth successful. Tokens are created in cookie.");
		return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
	}

	public String getUserName() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication.getName();
	}

	public String logout(HttpServletResponse response) {

		SecurityContextHolder.clearContext();

		response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.deleteAccessTokenCookie().toString());
		response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.deleteRefreshTokenCookie().toString());

		return "logout successfully";
	}

	private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
		httpHeaders.add(HttpHeaders.SET_COOKIE,
				cookieUtil.createAccessTokenCookie(token.getTokenValue(), token.getDuration()).toString());
	}

	private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
		httpHeaders.add(HttpHeaders.SET_COOKIE,
				cookieUtil.createRefreshTokenCookie(token.getTokenValue(), token.getDuration()).toString());
	}
}
