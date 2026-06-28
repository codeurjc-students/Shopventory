package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.model.*;
import es.codeurjc.shopventory.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Profile("!test")
public class DataInitializerService {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializerService.class);

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProviderRepository providerRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializerService(UserRepository userRepository, ProductRepository productRepository,
                                   ProviderRepository providerRepository, EmployeeRepository employeeRepository,
                                   OrderRepository orderRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.providerRepository = providerRepository;
        this.employeeRepository = employeeRepository;
        this.orderRepository = orderRepository;
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
        createEmployees();
        createOrders();
        LOG.info("Database initialization complete.");
    }

    // -------------------------------------------------------------------------
    // Users
    // -------------------------------------------------------------------------
    private void createUsers() {
        User admin = new User("admin@shopventory.com",
                passwordEncoder.encode("Admin1234!"), "Carlos", "García");
        admin.setRoles(List.of("ADMIN", "USER"));
        admin.setApproved(true);
        userRepository.save(admin);

        User user = new User("user@shopventory.com",
                passwordEncoder.encode("User1234!"), "Laura", "Martínez");
        user.setRoles(List.of("USER"));
        user.setApproved(true);
        userRepository.save(user);

        User pending = new User("pending@shopventory.com",
                passwordEncoder.encode("Pending1234!"), "Pedro", "López");
        pending.setRoles(List.of("USER"));
        pending.setApproved(false);
        userRepository.save(pending);
    }

    // -------------------------------------------------------------------------
    // Providers
    // -------------------------------------------------------------------------
    private void createProviders() {
        providerRepository.save(new Provider(
                "TechSupplies S.L.", "Calle Mayor 1, Madrid", "+34 911 234 567",
                "https://techsupplies.es", "Carlos García", "info@techsupplies.es",
                Set.of("Electronics", "Hardware", "Computers")));

        providerRepository.save(new Provider(
                "FoodDistrib S.A.", "Av. Industria 45, Barcelona", "+34 932 345 678",
                "https://fooddistrib.es", "Ana Martínez", "contacto@fooddistrib.es",
                Set.of("Food", "Beverages")));

        providerRepository.save(new Provider(
                "OfficeWorld S.L.", "Paseo de la Castellana 100, Madrid", "+34 914 567 890",
                "https://officeworld.es", "Roberto Sánchez", "ventas@officeworld.es",
                Set.of("Office", "Stationery", "Furniture")));

        providerRepository.save(new Provider(
                "SportGear S.A.", "Calle Deporte 22, Valencia", "+34 963 456 789",
                "https://sportgear.es", "Marta López", "pedidos@sportgear.es",
                Set.of("Sports", "Fitness", "Outdoors")));

        providerRepository.save(new Provider(
                "GlobalImport S.L.", "Av. del Puerto 5, Bilbao", "+34 944 321 654",
                "https://globalimport.es", "Diego Fernández", "import@globalimport.es",
                Set.of("Electronics", "Accessories", "Gadgets")));
    }

    // -------------------------------------------------------------------------
    // Products (16)
    // -------------------------------------------------------------------------
    private void createProducts() {
        Provider tech    = providerRepository.findByName("TechSupplies S.L.").orElseThrow();
        Provider food    = providerRepository.findByName("FoodDistrib S.A.").orElseThrow();
        Provider office  = providerRepository.findByName("OfficeWorld S.L.").orElseThrow();
        Provider sport   = providerRepository.findByName("SportGear S.A.").orElseThrow();
        Provider global  = providerRepository.findByName("GlobalImport S.L.").orElseThrow();

        // --- Electronics / Computers ---
        save("Laptop Pro 15",       "LAP-001", "High performance laptop for professionals",
                "Pro laptop 15\"", new BigDecimal("1299.99"), 25, 5,
                Set.of("Electronics", "Computers"), Set.of(tech));

        save("Laptop Air 13",       "LAP-002", "Ultra-thin lightweight laptop for everyday use",
                "Thin & light laptop 13\"", new BigDecimal("899.99"), 15, 4,
                Set.of("Electronics", "Computers"), Set.of(tech, global));

        save("27\" Monitor 4K",     "MON-001", "27-inch 4K UHD IPS monitor with HDR support",
                "4K IPS monitor 27\"", new BigDecimal("499.99"), 12, 3,
                Set.of("Electronics", "Monitors"), Set.of(tech));

        save("Webcam HD 1080p",     "CAM-001", "Full HD webcam with built-in microphone",
                "HD webcam 1080p", new BigDecimal("59.99"), 35, 8,
                Set.of("Electronics", "Peripherals"), Set.of(tech, global));

        save("Wireless Mouse",      "MOU-001", "Ergonomic wireless mouse 2.4GHz, 12-month battery",
                "Wireless ergonomic mouse", new BigDecimal("29.99"), 80, 10,
                Set.of("Electronics", "Peripherals"), Set.of(tech));

        save("Mechanical Keyboard", "KEY-001", "Compact TKL mechanical keyboard, Cherry MX Red switches",
                "TKL mechanical keyboard", new BigDecimal("89.99"), 40, 10,
                Set.of("Electronics", "Peripherals"), Set.of(tech, global));

        save("USB-C Hub 7-in-1",    "HUB-001", "7-port USB-C hub with HDMI, USB-A and PD charging",
                "7-in-1 USB-C hub", new BigDecimal("49.99"), 3, 5,
                Set.of("Electronics", "Accessories"), Set.of(global));

        save("Noise-Cancelling Headphones", "HDP-001", "Over-ear ANC headphones, 30h battery",
                "ANC over-ear headphones", new BigDecimal("149.99"), 20, 5,
                Set.of("Electronics", "Audio"), Set.of(global));

        // --- Food / Beverages ---
        save("Organic Coffee 1kg",  "COF-001", "Premium single-origin organic Arabica coffee beans",
                "Organic Arabica coffee 1kg", new BigDecimal("18.50"), 60, 15,
                Set.of("Food", "Beverages"), Set.of(food));

        save("Green Tea 50 bags",   "TEA-001", "Sencha green tea, individually wrapped bags",
                "Japanese sencha tea x50", new BigDecimal("8.99"), 45, 10,
                Set.of("Food", "Beverages"), Set.of(food));

        save("Protein Bar Pack x12","PRO-001", "High-protein energy bars, assorted flavours",
                "Protein bars x12 pack", new BigDecimal("24.99"), 4, 6,
                Set.of("Food", "Sports"), Set.of(food, sport));

        // --- Office / Furniture ---
        save("Ergonomic Chair",     "CHA-001", "Lumbar support ergonomic office chair, adjustable armrests",
                "Ergonomic office chair", new BigDecimal("299.99"), 8, 2,
                Set.of("Office", "Furniture"), Set.of(office));

        save("Standing Desk 140cm", "DES-001", "Height-adjustable standing desk, 140×70cm",
                "Height-adjustable desk 140cm", new BigDecimal("449.99"), 5, 2,
                Set.of("Office", "Furniture"), Set.of(office));

        save("Notebook A5 Pack x5", "NOT-001", "Lined A5 notebooks, 96 pages each",
                "A5 notebooks x5", new BigDecimal("12.99"), 120, 20,
                Set.of("Office", "Stationery"), Set.of(office));

        // --- Sports / Fitness ---
        save("Yoga Mat Premium",    "YOG-001", "Non-slip TPE yoga mat, 6mm thickness, includes carry strap",
                "Premium TPE yoga mat", new BigDecimal("34.99"), 30, 8,
                Set.of("Sports", "Fitness"), Set.of(sport));

        save("Resistance Bands Set","RES-001", "Set of 5 resistance bands, light to heavy resistance",
                "Resistance bands x5 set", new BigDecimal("19.99"), 3, 5,
                Set.of("Sports", "Fitness"), Set.of(sport));
    }

    private void save(String name, String sku, String description, String descriptionShort,
                      BigDecimal price, int stock, int minThreshold,
                      Set<String> categories, Set<Provider> providers) {
        Product p = new Product(name, sku, description, descriptionShort,
                price, stock, minThreshold, categories);
        p.setProviders(providers);
        productRepository.save(p);
    }

    // -------------------------------------------------------------------------
    // Employees (4)
    // -------------------------------------------------------------------------
    private void createEmployees() {
        employeeRepository.save(new Employee("Ana",    "González",  "ana.gonzalez@shopventory.com",
                "+34 611 001 001", "Store Manager",       LocalDate.of(2022, 1, 15)));
        employeeRepository.save(new Employee("Miguel", "Torres",    "miguel.torres@shopventory.com",
                "+34 611 002 002", "Sales Assistant",     LocalDate.of(2022, 3, 1)));
        employeeRepository.save(new Employee("Sofía",  "Rodríguez", "sofia.rodriguez@shopventory.com",
                "+34 611 003 003", "Inventory Manager",   LocalDate.of(2021, 6, 15)));
        employeeRepository.save(new Employee("Javier", "Morales",   "javier.morales@shopventory.com",
                "+34 611 004 004", "Customer Service",    LocalDate.of(2023, 2, 1)));
    }

    // -------------------------------------------------------------------------
    // Orders (6)
    // -------------------------------------------------------------------------
    private void createOrders() {
        User admin   = userRepository.findByEmail("admin@shopventory.com").orElseThrow();
        User standard = userRepository.findByEmail("user@shopventory.com").orElseThrow();

        Product laptop  = productRepository.findBySku("LAP-001").orElseThrow();
        Product mouse   = productRepository.findBySku("MOU-001").orElseThrow();
        Product coffee  = productRepository.findBySku("COF-001").orElseThrow();
        Product tea     = productRepository.findBySku("TEA-001").orElseThrow();
        Product keyboard = productRepository.findBySku("KEY-001").orElseThrow();
        Product hub     = productRepository.findBySku("HUB-001").orElseThrow();
        Product webcam  = productRepository.findBySku("CAM-001").orElseThrow();
        Product protein = productRepository.findBySku("PRO-001").orElseThrow();
        Product chair   = productRepository.findBySku("CHA-001").orElseThrow();
        Product yoga    = productRepository.findBySku("YOG-001").orElseThrow();

        Provider tech  = providerRepository.findByName("TechSupplies S.L.").orElseThrow();
        Provider food  = providerRepository.findByName("FoodDistrib S.A.").orElseThrow();
        Provider office = providerRepository.findByName("OfficeWorld S.L.").orElseThrow();

        // --- SALE 1: DELIVERED — Laptop + Mouse ---
        Order sale1 = new Order(OrderType.SALE, standard);
        sale1.setCustomerName("Tech Corp S.A.");
        sale1.setCustomerEmail("compras@techcorp.es");
        sale1.setOrderDate(LocalDateTime.now().minusDays(20));
        sale1.setDeliveryDate(LocalDate.now().minusDays(15));
        sale1.getItems().add(new OrderItem(sale1, laptop,  1, laptop.getPrice()));
        sale1.getItems().add(new OrderItem(sale1, mouse,   2, mouse.getPrice()));
        sale1.recalculateTotal();
        sale1.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(sale1);

        // --- SALE 2: DELIVERED — Coffee + Tea ---
        Order sale2 = new Order(OrderType.SALE, standard);
        sale2.setCustomerName("Oficinas Central");
        sale2.setCustomerEmail("admin@oficinas.es");
        sale2.setOrderDate(LocalDateTime.now().minusDays(12));
        sale2.setDeliveryDate(LocalDate.now().minusDays(8));
        sale2.getItems().add(new OrderItem(sale2, coffee, 3, coffee.getPrice()));
        sale2.getItems().add(new OrderItem(sale2, tea,    2, tea.getPrice()));
        sale2.recalculateTotal();
        sale2.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(sale2);

        // --- SALE 3: CONFIRMED — Keyboard + Hub ---
        Order sale3 = new Order(OrderType.SALE, standard);
        sale3.setCustomerName("María Jiménez");
        sale3.setCustomerEmail("maria.jimenez@gmail.com");
        sale3.setOrderDate(LocalDateTime.now().minusDays(5));
        sale3.getItems().add(new OrderItem(sale3, keyboard, 1, keyboard.getPrice()));
        sale3.getItems().add(new OrderItem(sale3, hub,      2, hub.getPrice()));
        sale3.recalculateTotal();
        sale3.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(sale3);

        // --- SALE 4: PENDING — Yoga + Chair ---
        Order sale4 = new Order(OrderType.SALE, standard);
        sale4.setCustomerName("Fitness Studio SL");
        sale4.setCustomerEmail("info@fitnessstudio.es");
        sale4.setOrderDate(LocalDateTime.now().minusDays(2));
        sale4.getItems().add(new OrderItem(sale4, yoga,  3, yoga.getPrice()));
        sale4.getItems().add(new OrderItem(sale4, chair, 1, chair.getPrice()));
        sale4.recalculateTotal();
        orderRepository.save(sale4);

        // --- PURCHASE 1: CONFIRMED — Webcam restock from TechSupplies ---
        Order purchase1 = new Order(OrderType.PURCHASE, admin);
        purchase1.setProvider(tech);
        purchase1.setNotes("Restocking webcams after high demand");
        purchase1.setOrderDate(LocalDateTime.now().minusDays(10));
        purchase1.getItems().add(new OrderItem(purchase1, webcam, 10, new BigDecimal("42.00")));
        purchase1.getItems().add(new OrderItem(purchase1, hub,     5, new BigDecimal("32.00")));
        purchase1.recalculateTotal();
        purchase1.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(purchase1);

        // --- PURCHASE 2: DELIVERED — Food restock from FoodDistrib ---
        Order purchase2 = new Order(OrderType.PURCHASE, admin);
        purchase2.setProvider(food);
        purchase2.setNotes("Monthly food supply order");
        purchase2.setOrderDate(LocalDateTime.now().minusDays(30));
        purchase2.setDeliveryDate(LocalDate.now().minusDays(25));
        purchase2.getItems().add(new OrderItem(purchase2, protein, 20, new BigDecimal("16.00")));
        purchase2.getItems().add(new OrderItem(purchase2, tea,     30, new BigDecimal("5.50")));
        purchase2.recalculateTotal();
        purchase2.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(purchase2);

        // --- PURCHASE 3: PENDING — Furniture from OfficeWorld ---
        Order purchase3 = new Order(OrderType.PURCHASE, admin);
        purchase3.setProvider(office);
        purchase3.setNotes("Q2 office furniture order");
        purchase3.setOrderDate(LocalDateTime.now().minusDays(1));
        purchase3.getItems().add(new OrderItem(purchase3, chair, 5, new BigDecimal("210.00")));
        purchase3.recalculateTotal();
        orderRepository.save(purchase3);
    }
}
