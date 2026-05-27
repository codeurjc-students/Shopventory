package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.dto.UserRegistrationDTO;
import es.codeurjc.shopventory.dto.UserResponseDTO;
import es.codeurjc.shopventory.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management endpoints (Admin only)")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "List all users paginated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User list returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponseDTO>> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(userService.search(search, pageable));
        }
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Update user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user data",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody UserRegistrationDTO dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Approve pending user registration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User approved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> approve(@PathVariable Long id) {
        return ResponseEntity.ok(userService.approve(id));
    }

    @Operation(summary = "Enable or disable a user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account status updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    @PostMapping("/{id}/enabled")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> setEnabled(@PathVariable Long id,
                                                       @RequestParam boolean value) {
        return ResponseEntity.ok(userService.setEnabled(id, value));
    }

    @Operation(summary = "Get list of users pending approval")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending users list returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content)
    })
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> pending() {
        return ResponseEntity.ok(userService.getPendingApprovals());
    }
}
