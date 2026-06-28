package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.EmployeeDTO;
import es.codeurjc.shopventory.exception.ConflictException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.Employee;
import es.codeurjc.shopventory.model.User;
import es.codeurjc.shopventory.repository.EmployeeRepository;
import es.codeurjc.shopventory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employeeDTO = new EmployeeDTO();
        employeeDTO.setName("Jane");
        employeeDTO.setSurname("Smith");
        employeeDTO.setEmail("jane@company.com");
        employeeDTO.setPosition("Warehouse Manager");
    }

    @Test
    void create_newEmployee_success() {
        when(employeeRepository.existsByEmail("jane@company.com")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.create(employeeDTO);

        assertNotNull(result);
        assertEquals("Jane", result.getName());
        assertEquals("Smith", result.getSurname());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void create_duplicateEmail_throwsConflict() {
        when(employeeRepository.existsByEmail("jane@company.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> employeeService.create(employeeDTO));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void create_withoutEmail_skipsUniquenessCheck() {
        employeeDTO.setEmail(null);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.create(employeeDTO);

        assertNotNull(result);
        verify(employeeRepository, never()).existsByEmail(any());
    }

    @Test
    void create_withUserId_linksUser() {
        employeeDTO.setEmail(null); // isolate the userId branch
        employeeDTO.setUserId(7L);
        User user = new User("jane@company.com", "pass", "Jane", "Smith");
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.create(employeeDTO);

        assertSame(user, result.getUser());
    }

    @Test
    void create_withUnknownUserId_throwsResourceNotFound() {
        employeeDTO.setEmail(null);
        employeeDTO.setUserId(999L);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.create(employeeDTO));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void findById_notFound_throwsResourceNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.findById(999L));
    }

    @Test
    void update_newEmailAlreadyInUse_throwsConflict() {
        Employee existing = new Employee("Jane", "Smith", "jane@company.com", null, "Manager", null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(existing));
        employeeDTO.setEmail("taken@company.com");
        when(employeeRepository.existsByEmail("taken@company.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> employeeService.update(1L, employeeDTO));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void delete_notFound_throwsResourceNotFound() {
        when(employeeRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> employeeService.delete(999L));
        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    void delete_existingEmployee_success() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> employeeService.delete(1L));
        verify(employeeRepository).deleteById(1L);
    }
}
