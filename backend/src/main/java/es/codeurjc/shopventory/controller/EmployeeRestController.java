package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.EmployeeDTO;
import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.model.Employee;
import es.codeurjc.shopventory.service.EmployeeService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Employees", description = "Employee management endpoints (Admin only)")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "List all employees paginated")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee list returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content)
    })
    @GetMapping
    public ResponseEntity<PageResponse<Employee>> list(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(employeeService.search(search, pageable));
        }
        return ResponseEntity.ok(employeeService.findAll(pageable));
    }

    @Operation(summary = "Get employee by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Employee not found",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @Operation(summary = "Create a new employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employee created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
        @ApiResponse(responseCode = "400", description = "Invalid employee data",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content)
    })
    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(dto));
    }

    @Operation(summary = "Update employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))),
        @ApiResponse(responseCode = "400", description = "Invalid employee data",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Employee not found",
            content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.ok(employeeService.update(id, dto));
    }

    @Operation(summary = "Delete employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Employee deleted",
            content = @Content),
        @ApiResponse(responseCode = "403", description = "Admin role required",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Employee not found",
            content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
