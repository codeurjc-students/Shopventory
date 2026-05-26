package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.UserRegistrationDTO;
import es.codeurjc.shopventory.dto.UserResponseDTO;
import es.codeurjc.shopventory.exception.ConflictException;
import es.codeurjc.shopventory.model.User;
import es.codeurjc.shopventory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDTO registrationDTO;

    @BeforeEach
    void setUp() {
        registrationDTO = new UserRegistrationDTO();
        registrationDTO.setEmail("test@test.com");
        registrationDTO.setPassword("Password123!");
        registrationDTO.setName("Test");
        registrationDTO.setSurname("User");
    }

    @Test
    void register_newUser_success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        User savedUser = new User("test@test.com", "encodedPass", "Test", "User");
        savedUser.setApproved(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO result = userService.register(registrationDTO);

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
        assertFalse(result.isApproved());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsConflict() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.register(registrationDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void approve_existingUser_setsApproved() {
        User user = new User("test@test.com", "pass", "Test", "User");
        user.setApproved(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO result = userService.approve(1L);

        assertTrue(result.isApproved());
    }
}
