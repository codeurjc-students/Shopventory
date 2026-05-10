package es.codeurjc.shopventory.dto;

import es.codeurjc.shopventory.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponseDTO {

    private Long id;
    private String email;
    private String name;
    private String surname;
    private String phone;
    private List<String> roles;
    private boolean approved;
    private boolean enabled;
    private LocalDateTime createdAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.phone = user.getPhone();
        this.roles = user.getRoles();
        this.approved = user.isApproved();
        this.enabled = user.isEnabled();
        this.createdAt = user.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhone() { return phone; }
    public List<String> getRoles() { return roles; }
    public boolean isApproved() { return approved; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
