package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.OrderDTO;
import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.exception.BadRequestException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.*;
import es.codeurjc.shopventory.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final StockMovementService stockMovementService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        ProviderRepository providerRepository, UserRepository userRepository,
                        StockMovementService stockMovementService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
        this.stockMovementService = stockMovementService;
    }

    public Order create(OrderDTO dto, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order(dto.getType(), creator);
        order.setDeliveryDate(dto.getDeliveryDate());
        order.setNotes(dto.getNotes());
        if (dto.getDiscount() != null) order.setDiscount(dto.getDiscount());
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerEmail(dto.getCustomerEmail());

        if (dto.getProviderId() != null) {
            Provider provider = providerRepository.findById(dto.getProviderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Provider", dto.getProviderId()));
            order.setProvider(provider);
        }

        for (var itemDto : dto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemDto.getProductId()));

            if (dto.getType() == OrderType.SALE && product.getStock() < itemDto.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName()
                        + ". Available: " + product.getStock() + ", requested: " + itemDto.getQuantity());
            }

            OrderItem item = new OrderItem(order, product, itemDto.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }

        order.recalculateTotal();
        return orderRepository.save(order);
    }

    public Order confirm(Long id, String confirmerEmail) {
        Order order = getOrderOrThrow(id);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is not in PENDING status");
        }
        User confirmer = userRepository.findByEmail(confirmerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        order.setStatus(OrderStatus.CONFIRMED);
        Order saved = orderRepository.save(order);

        // For SALE orders, verify stock is still sufficient at confirmation time
        if (order.getType() == OrderType.SALE) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product.getStock() < item.getQuantity()) {
                    throw new BadRequestException(
                        "Insufficient stock for \"" + product.getName() + "\". " +
                        "Available: " + product.getStock() + ", required: " + item.getQuantity());
                }
            }
        }

        // Update stock based on order type
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            int stockBefore = product.getStock();
            int stockAfter;
            StockMovementType movementType;

            if (order.getType() == OrderType.PURCHASE) {
                stockAfter = stockBefore + item.getQuantity();
                movementType = StockMovementType.PURCHASE_IN;
            } else {
                stockAfter = stockBefore - item.getQuantity();
                movementType = StockMovementType.SALE_OUT;
            }

            product.setStock(stockAfter);
            productRepository.save(product);
            int delta = stockAfter - stockBefore;
            stockMovementService.recordMovement(product, delta, stockBefore, stockAfter,
                    movementType, "Order #" + order.getId(), confirmer);
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public PageResponse<Order> findAll(Pageable pageable) {
        return new PageResponse<>(orderRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return getOrderOrThrow(id);
    }

    @Transactional(readOnly = true)
    public PageResponse<Order> findByType(OrderType type, Pageable pageable) {
        return new PageResponse<>(orderRepository.findByType(type, pageable));
    }

    @Transactional(readOnly = true)
    public PageResponse<Order> findByUser(Long userId, Pageable pageable) {
        return new PageResponse<>(orderRepository.findByCreatedById(userId, pageable));
    }

    public Order cancel(Long id) {
        Order order = getOrderOrThrow(id);
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel a delivered order");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order", id);
        }
        orderRepository.deleteById(id);
    }

    private Order getOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }
}
