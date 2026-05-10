package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.UserRegistrationDTO;
import es.codeurjc.shopventory.dto.UserResponseDTO;
import es.codeurjc.shopventory.exception.ConflictException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.User;
import es.codeurjc.shopventory.repository.UserRepository;
import es.codeurjc.shopventory.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO register(UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already registered: " + dto.getEmail());
        }
        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getName(),
                dto.getSurname()
        );
        user.setPhone(dto.getPhone());
        user.getRoles().add("USER");
        user.setApproved(false);
        return new UserResponseDTO(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponseDTO> findAll(Pageable pageable) {
        Page<UserResponseDTO> page = userRepository.findAll(pageable).map(UserResponseDTO::new);
        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        return new UserResponseDTO(getUserOrThrow(id));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        return new UserResponseDTO(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponseDTO> search(String query, Pageable pageable) {
        Page<UserResponseDTO> page = userRepository
                .findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query, pageable)
                .map(UserResponseDTO::new);
        return new PageResponse<>(page);
    }

    public UserResponseDTO approve(Long id) {
        User user = getUserOrThrow(id);
        user.setApproved(true);
        return new UserResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO update(Long id, UserRegistrationDTO dto) {
        User user = getUserOrThrow(id);
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already in use: " + dto.getEmail());
        }
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setPhone(dto.getPhone());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setEncodedPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return new UserResponseDTO(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
    }

    public UserResponseDTO setEnabled(Long id, boolean enabled) {
        User user = getUserOrThrow(id);
        user.setEnabled(enabled);
        return new UserResponseDTO(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getPendingApprovals() {
        return userRepository.findByApprovedFalse().stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
