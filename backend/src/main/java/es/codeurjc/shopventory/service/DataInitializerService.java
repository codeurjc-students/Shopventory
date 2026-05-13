package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.model.Product;
import es.codeurjc.shopventory.model.Provider;
import es.codeurjc.shopventory.model.User;
import es.codeurjc.shopventory.repository.ProductRepository;
import es.codeurjc.shopventory.repository.ProviderRepository;
import es.codeurjc.shopventory.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Profile("!test")
public class DataInitializerService {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializerService.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializerService(UserRepository userRepository, ProductRepository productRepository,
                                   ProviderRepository providerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.providerRepository = providerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        if (userRepository.existsByEmail("admin@shopventory.com")) {
            LOG.info("Database already initialized, skipping seed data.");
            return;
        }

        LOG.info("Initializing database with sample data...");
        createUsers();
        createProviders();
        createProducts();
        LOG.info("Database initialization complete.");
    }

    private void createUsers() {
        User admin = new User("admin@shopventory.com",
                passwordEncoder.encode("Admin1234!"), "Admin", "Shopventory");
        admin.setRoles(List.of("ADMIN", "USER"));
        admin.setApproved(true);
        userRepository.save(admin);

        User user = new User("user@shopventory.com",
                passwordEncoder.encode("User1234!"), "Standard", "User");
        user.setRoles(List.of("USER"));
        user.setApproved(true);
        userRepository.save(user);

        User pending = new User("pending@shopventory.com",
                passwordEncoder.encode("Pending1234!"), "Pending", "User");
        pending.setRoles(List.of("USER"));
        pending.setApproved(false);
        userRepository.save(pending);
    }

    private void createProviders() {
        Provider p1 = new Provider("TechSupplies S.L.", "Calle Mayor 1, Madrid", "+34 911 234 567",
                "https://techsupplies.es", "Carlos García", "info@techsupplies.es",
                Set.of("Electronics", "Hardware"));
        providerRepository.save(p1);

        Provider p2 = new Provider("FoodDistrib S.A.", "Av. Industria 45, Barcelona", "+34 932 345 678",
                "https://fooddistrib.es", "Ana Martínez", "contacto@fooddistrib.es",
                Set.of("Food", "Beverages"));
        providerRepository.save(p2);
    }

    private void createProducts() {
        Product p1 = new Product("Laptop Pro 15", "LAP-001", "High performance laptop for professionals",
                "Pro laptop 15 inch", new BigDecimal("1299.99"), 25, 5,
                Set.of("Electronics", "Computers"));
        productRepository.save(p1);

        Product p2 = new Product("Wireless Mouse", "MOU-001", "Ergonomic wireless mouse with 2.4GHz",
                "Wireless ergonomic mouse", new BigDecimal("29.99"), 80, 10,
                Set.of("Electronics", "Peripherals"));
        productRepository.save(p2);

        Product p3 = new Product("USB-C Hub 7-in-1", "HUB-001", "7-port USB-C hub with HDMI and PD charging",
                "7-in-1 USB-C hub", new BigDecimal("49.99"), 3, 5,
                Set.of("Electronics", "Accessories"));
        productRepository.save(p3);

        Product p4 = new Product("Organic Coffee 1kg", "COF-001", "Premium organic Arabica coffee beans",
                "Organic Arabica coffee 1kg", new BigDecimal("18.50"), 60, 15,
                Set.of("Food", "Beverages"));
        productRepository.save(p4);
    }
}
