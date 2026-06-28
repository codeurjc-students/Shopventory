package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.DashboardStatsDTO;
import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.model.StockMovement;
import es.codeurjc.shopventory.service.DashboardService;
import es.codeurjc.shopventory.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Analytics and statistics endpoints")
public class DashboardRestController {

    private final DashboardService dashboardService;
    private final StockMovementService stockMovementService;

    public DashboardRestController(DashboardService dashboardService,
                                    StockMovementService stockMovementService) {
        this.dashboardService = dashboardService;
        this.stockMovementService = stockMovementService;
    }

    @Operation(summary = "Get dashboard statistics summary")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard statistics returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardStatsDTO.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content)
    })
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> stats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @Operation(summary = "Get all stock movements paginated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock movement list returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated",
            content = @Content)
    })
    @GetMapping("/stock-movements")
    public ResponseEntity<PageResponse<StockMovement>> stockMovements(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(stockMovementService.findAll(pageable));
    }
}
