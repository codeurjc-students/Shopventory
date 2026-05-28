package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.dto.ProductDTO;
import es.codeurjc.shopventory.dto.StockUpdateDTO;
import es.codeurjc.shopventory.model.Product;
import es.codeurjc.shopventory.model.StockMovement;
import es.codeurjc.shopventory.service.ProductService;
import es.codeurjc.shopventory.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "List all products paginated, optionally filtered by search, category or provider")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product list returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PageResponse<Product>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long providerId,
            @PageableDefault(size = 10) Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(productService.search(search, pageable));
        }
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(productService.findByCategory(category, pageable));
        }
        if (providerId != null) {
            return ResponseEntity.ok(productService.findByProvider(providerId, pageable));
        }
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @Operation(summary = "Get product by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "Get low-stock products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Low-stock product list returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
    })
    @GetMapping("/low-stock")
    public ResponseEntity<PageResponse<Product>> lowStock(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.findLowStock(pageable));
    }

    @Operation(summary = "Create a new product (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "400", description = "Invalid product data",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "409", description = "SKU already exists",
            content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> create(@Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }

    @Operation(summary = "Update product (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "400", description = "Invalid product data",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @Operation(summary = "Delete product (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get product image")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image returned",
            content = @Content(mediaType = "image/jpeg")),
        @ApiResponse(responseCode = "404", description = "Product or image not found",
            content = @Content)
    })
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws SQLException, IOException {
        byte[] image = productService.getImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }

    @Operation(summary = "Upload product image (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image uploaded",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
        @ApiResponse(responseCode = "400", description = "Invalid file",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content)
    })
    @PutMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> uploadImage(@PathVariable Long id,
                                                @RequestParam("file") MultipartFile file)
            throws IOException, SQLException {
        return ResponseEntity.ok(productService.updateImage(id, file));
    }

    @Operation(summary = "Manually adjust product stock")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock updated, movement recorded",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StockMovement.class))),
        @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient stock",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content)
    })
    @PostMapping("/{id}/stock")
    public ResponseEntity<StockMovement> updateStock(
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                stockMovementService.manualUpdate(id, dto, userDetails.getUsername()));
    }

    @Operation(summary = "Get stock movement history for a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock movement history returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
        @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content)
    })
    @GetMapping("/{id}/stock-movements")
    public ResponseEntity<PageResponse<StockMovement>> stockHistory(
            @PathVariable Long id,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(stockMovementService.findByProduct(id, pageable));
    }
}
