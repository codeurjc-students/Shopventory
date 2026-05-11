package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.EmployeeDTO;
import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.exception.ConflictException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.Employee;
import es.codeurjc.shopventory.model.User;
import es.codeurjc.shopventory.repository.EmployeeRepository;
import es.codeurjc.shopventory.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public EmployeeService(EmployeeRepository employeeRepository, UserRepository userRepository) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    public Employee create(EmployeeDTO dto) {
        if (dto.getEmail() != null && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Employee email already exists: " + dto.getEmail());
        }
        Employee employee = mapDtoToEmployee(new Employee(), dto);
        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public PageResponse<Employee> findAll(Pageable pageable) {
        return new PageResponse<>(employeeRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return getEmployeeOrThrow(id);
    }

    @Transactional(readOnly = true)
    public PageResponse<Employee> search(String query, Pageable pageable) {
        return new PageResponse<>(
                employeeRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(
                        query, query, pageable));
    }

    public Employee update(Long id, EmployeeDTO dto) {
        Employee employee = getEmployeeOrThrow(id);
        if (dto.getEmail() != null && !dto.getEmail().equals(employee.getEmail())
                && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email already in use: " + dto.getEmail());
        }
        return employeeRepository.save(mapDtoToEmployee(employee, dto));
    }

    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", id);
        }
        employeeRepository.deleteById(id);
    }

    private Employee mapDtoToEmployee(Employee employee, EmployeeDTO dto) {
        employee.setName(dto.getName());
        employee.setSurname(dto.getSurname());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setPosition(dto.getPosition());
        employee.setHireDate(dto.getHireDate());
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", dto.getUserId()));
            employee.setUser(user);
        }
        return employee;
    }

    private Employee getEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }
}
