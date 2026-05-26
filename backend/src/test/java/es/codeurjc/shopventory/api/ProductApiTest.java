package es.codeurjc.shopventory.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.shopventory.dto.ProductDTO;
import es.codeurjc.shopventory.model.User;
import es.codeurjc.shopventory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        if (!userRepository.existsByEmail("admin@test.com")) {
            User admin = new User("admin@test.com", passwordEncoder.encode("Admin1234!"), "Admin", "Test");
            admin.setRoles(List.of("ADMIN", "USER"));
            admin.setApproved(true);
            userRepository.save(admin);
        }
        userRepository.findByEmail("newuser@test.com").ifPresent(userRepository::delete);
    }

    @Test
    void getProducts_authenticated_returnsPage() throws Exception {
        mockMvc.perform(get("/api/products")
                .with(user("admin@test.com").roles("ADMIN", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void getProducts_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProduct_asAdmin_returns201() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("API Test Product");
        dto.setSku("API-TEST-001");
        dto.setPrice(new BigDecimal("9.99"));
        dto.setStock(10);
        dto.setMinStockThreshold(2);
        dto.setCategories(Set.of("Test"));

        mockMvc.perform(post("/api/products")
                .with(user("admin@test.com").roles("ADMIN", "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("API Test Product"))
                .andExpect(jsonPath("$.sku").value("API-TEST-001"));
    }

    @Test
    void createProduct_asRegularUser_returns403() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Unauthorized Product");
        dto.setPrice(new BigDecimal("5.00"));

        mockMvc.perform(post("/api/products")
                .with(user("user@test.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getLowStockProducts_authenticated_returnsPage() throws Exception {
        mockMvc.perform(get("/api/products/low-stock")
                .with(user("admin@test.com").roles("ADMIN", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void register_newUser_returns201() throws Exception {
        String body = """
            {
              "email": "newuser@test.com",
              "password": "Password123!",
              "name": "New",
              "surname": "User"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.approved").value(false));
    }

    @Test
    void getDashboardStats_authenticated_returnsStats() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats")
                .with(user("admin@test.com").roles("ADMIN", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").isNumber())
                .andExpect(jsonPath("$.totalUsers").isNumber());
    }
}
