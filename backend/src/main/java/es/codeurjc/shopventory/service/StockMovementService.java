package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.dto.StockUpdateDTO;
import es.codeurjc.shopventory.exception.BadRequestException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.*;
import es.codeurjc.shopventory.repository.ProductRepository;
import es.codeurjc.shopventory.repository.StockMovementRepository;
import es.codeurjc.shopventory.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public StockMovementService(StockMovementRepository stockMovementRepository,
                                ProductRepository productRepository,
                                UserRepository userRepository,
                                EmailService emailService) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public StockMovement manualUpdate(Long productId, StockUpdateDTO dto, String performerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        User performer = userRepository.findByEmail(performerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int stockBefore = product.getStock();
        int qty = dto.getQuantity();
        // Positive value → set stock to that exact amount
        // Negative value → reduce current stock by that amount
        int stockAfter = qty >= 0 ? qty : stockBefore + qty;
        int delta = stockAfter - stockBefore;

        if (stockAfter < 0) {
            throw new BadRequestException(
                    "Insufficient stock. Current: " + stockBefore + ", reduction: " + Math.abs(qty));
        }

        product.setStock(stockAfter);
        productRepository.save(product);

        if (stockBefore > product.getMinStockThreshold() && stockAfter <= product.getMinStockThreshold()) {
            var adminEmails = userRepository.findAllAdmins().stream()
                    .map(u -> u.getEmail()).toList();
            emailService.sendLowStockAlert(adminEmails, product.getName(), product.getSku(),
                    stockAfter, product.getMinStockThreshold());
        }

        StockMovement movement = new StockMovement(
                product, delta, stockBefore, stockAfter,
                StockMovementType.MANUAL_ADJUSTMENT,
                dto.getReason(), performer);
        return stockMovementRepository.save(movement);
    }

    public StockMovement recordMovement(Product product, int quantity, int stockBefore, int stockAfter,
                                        StockMovementType type, String reason, User performer) {
        StockMovement movement = new StockMovement(product, quantity, stockBefore, stockAfter,
                type, reason, performer);
        return stockMovementRepository.save(movement);
    }

    @Transactional(readOnly = true)
    public PageResponse<StockMovement> findAll(Pageable pageable) {
        return new PageResponse<>(stockMovementRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public PageResponse<StockMovement> findByProduct(Long productId, Pageable pageable) {
        return new PageResponse<>(
                stockMovementRepository.findByProductIdOrderByDateDesc(productId, pageable));
    }
}
