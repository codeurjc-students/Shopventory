package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.dto.ProductDTO;
import es.codeurjc.shopventory.dto.StockUpdateDTO;
import es.codeurjc.shopventory.model.Product;
import es.codeurjc.shopventory.model.StockMovement;
import es.codeurjc.shopventory.service.ProductService;
import es.codeurjc.shopventory.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductRestController {

    private final ProductService productService;
    private final StockMovementService stockMovementService;

    public ProductRestController(ProductService productService,
                                  StockMovementService stockMovementService) {
        this.productService = productService;
        this.stockMovementService = stockMovementService;
    }

    @Operation(summary = "List all products paginated, optionally filtered by category")
    @GetMapping
    public ResponseEntity<PageResponse<Product>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 10) Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(productService.search(search, pageable));
        }
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(productService.findByCategory(category, pageable));
        }
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "Get low-stock products")
    @GetMapping("/low-stock")
    public ResponseEntity<PageResponse<Product>> lowStock(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.findLowStock(pageable));
    }

    @Operation(summary = "Create a new product (Admin only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> create(@Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }

    @Operation(summary = "Update product (Admin only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @Operation(summary = "Delete product (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get product image")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws SQLException, IOException {
        byte[] image = productService.getImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @Operation(summary = "Upload product image")
    @PutMapping("/{id}/image")
    public ResponseEntity<Product> uploadImage(@PathVariable Long id,
                                                @RequestParam("file") MultipartFile file)
            throws IOException, SQLException {
        return ResponseEntity.ok(productService.updateImage(id, file));
    }

    @Operation(summary = "Manually adjust product stock")
    @PostMapping("/{id}/stock")
    public ResponseEntity<StockMovement> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                stockMovementService.manualUpdate(id, dto, userDetails.getUsername()));
    }

    @Operation(summary = "Get stock movement history for a product")
    @GetMapping("/{id}/stock-movements")
    public ResponseEntity<PageResponse<StockMovement>> stockHistory(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(stockMovementService.findByProduct(id, pageable));
    }
}
