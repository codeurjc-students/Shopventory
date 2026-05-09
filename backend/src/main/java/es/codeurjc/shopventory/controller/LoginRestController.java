package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.UserRegistrationDTO;
import es.codeurjc.shopventory.dto.UserResponseDTO;
import es.codeurjc.shopventory.security.jwt.AuthResponse;
import es.codeurjc.shopventory.security.jwt.LoginRequest;
import es.codeurjc.shopventory.security.jwt.UserLoginService;
import es.codeurjc.shopventory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login, logout and registration endpoints")
public class LoginRestController {

    private final UserLoginService userLoginService;
    private final UserService userService;

    public LoginRestController(UserLoginService userLoginService, UserService userService) {
        this.userLoginService = userLoginService;
        this.userService = userService;
    }

    @Operation(summary = "Login with email and password")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            @CookieValue(name = "AuthToken", required = false) String accessToken,
            @CookieValue(name = "RefreshToken", required = false) String refreshToken) {
        return userLoginService.login(loginRequest, accessToken, refreshToken);
    }

    @Operation(summary = "Refresh access token using refresh token cookie")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken) {
        return userLoginService.refresh(refreshToken);
    }

    @Operation(summary = "Logout and clear session cookies")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String result = userLoginService.logout(request, response);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get current authenticated user info")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDTO user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Register a new user account (pending admin approval)")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegistrationDTO dto) {
        UserResponseDTO created = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
