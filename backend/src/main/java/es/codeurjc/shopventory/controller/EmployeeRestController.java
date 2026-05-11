package es.codeurjc.shopventory.controller;

import es.codeurjc.shopventory.dto.EmployeeDTO;
import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.model.Employee;
import es.codeurjc.shopventory.service.EmployeeService;
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
@RequestMapping("/api/employees")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Employees", description = "Employee management endpoints (Admin only)")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "List all employees paginated")
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
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @Operation(summary = "Create a new employee")
    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(dto));
    }

    @Operation(summary = "Update employee")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.ok(employeeService.update(id, dto));
    }

    @Operation(summary = "Delete employee")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
