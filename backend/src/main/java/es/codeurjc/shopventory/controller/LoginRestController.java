package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.UserRegistrationDTO;
import es.codeurjc.shopventory.dto.UserResponseDTO;
import es.codeurjc.shopventory.security.jwt.AuthResponse;
import es.codeurjc.shopventory.security.jwt.LoginRequest;
import es.codeurjc.shopventory.security.jwt.UserLoginService;
import es.codeurjc.shopventory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful, tokens set in cookies",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials or account not approved",
            content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            @CookieValue(name = "AuthToken", required = false) String accessToken,
            @CookieValue(name = "RefreshToken", required = false) String refreshToken) {
        return userLoginService.login(loginRequest, accessToken, refreshToken);
    }

    @Operation(summary = "Refresh access token using refresh token cookie")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token",
            content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken) {
        return userLoginService.refresh(refreshToken);
    }

    @Operation(summary = "Logout and clear session cookies")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logged out successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        String result = userLoginService.logout(response);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get current authenticated user info")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current user data returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDTO user = userService.findByEmail(userDetails.getUsername());
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Register a new user account (pending admin approval)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered, pending approval",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid registration data",
            content = @Content),
        @ApiResponse(responseCode = "409", description = "Email already registered",
            content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegistrationDTO dto) {
        UserResponseDTO created = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
