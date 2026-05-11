package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.dto.ProviderDTO;
import es.codeurjc.shopventory.model.Provider;
import es.codeurjc.shopventory.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers")
@Tag(name = "Providers", description = "Supplier/provider management endpoints")
public class ProviderRestController {

    private final ProviderService providerService;

    public ProviderRestController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @Operation(summary = "List all providers paginated")
    @GetMapping
    public ResponseEntity<PageResponse<Provider>> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(providerService.search(search, pageable));
        }
        return ResponseEntity.ok(providerService.findAll(pageable));
    }

    @Operation(summary = "Get provider by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getById(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.findById(id));
    }

    @Operation(summary = "Create a new provider (Admin only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Provider> create(@Valid @RequestBody ProviderDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(providerService.create(dto));
    }

    @Operation(summary = "Update provider (Admin only)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Provider> update(@PathVariable Long id, @Valid @RequestBody ProviderDTO dto) {
        return ResponseEntity.ok(providerService.update(id, dto));
    }

    @Operation(summary = "Delete provider (Admin only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        providerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
