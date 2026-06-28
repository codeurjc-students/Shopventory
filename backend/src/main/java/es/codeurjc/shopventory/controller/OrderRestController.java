package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.OrderDTO;
import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.model.Order;
import es.codeurjc.shopventory.model.OrderType;
import es.codeurjc.shopventory.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Sales and purchase order endpoints")
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "List all orders paginated, optionally filtered by type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order list returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content)
    })
    @GetMapping
    public ResponseEntity<PageResponse<Order>> list(
            @RequestParam(required = false) OrderType type,
            @PageableDefault(size = 10) Pageable pageable) {
        if (type != null) {
            return ResponseEntity.ok(orderService.findByType(type, pageable));
        }
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @Operation(summary = "Get order by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "400", description = "Invalid order data or insufficient stock",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Product or provider not found",
            content = @Content)
    })
    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody OrderDTO dto,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(dto, userDetails.getUsername()));
    }

    @Operation(summary = "Confirm order and update stock automatically (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order confirmed and stock updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "400", description = "Order is not in PENDING status",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content)
    })
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> confirm(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.confirm(id, userDetails.getUsername()));
    }

    @Operation(summary = "Mark a confirmed order as delivered and set delivery date to today (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order marked as delivered",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "400", description = "Order is not in CONFIRMED status",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content)
    })
    @PostMapping("/{id}/deliver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Order> deliver(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.deliver(id));
    }

    @Operation(summary = "Cancel an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled (already delivered)",
            content = @Content),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content)
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Order> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }

    @Operation(summary = "Delete order (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deleted",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
