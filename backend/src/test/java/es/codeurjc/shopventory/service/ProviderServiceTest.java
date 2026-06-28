package es.codeurjc.shopventory.service;

import es.codeurjc.shopventory.dto.PageResponse;
import es.codeurjc.shopventory.dto.ProviderDTO;
import es.codeurjc.shopventory.exception.ConflictException;
import es.codeurjc.shopventory.exception.ResourceNotFoundException;
import es.codeurjc.shopventory.model.Provider;
import es.codeurjc.shopventory.repository.ProductRepository;
import es.codeurjc.shopventory.repository.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProviderServiceTest {

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProviderService providerService;

    private ProviderDTO providerDTO;
    private Provider existingProvider;

    @BeforeEach
    void setUp() {
        providerDTO = new ProviderDTO();
        providerDTO.setName("Acme Corp");
        providerDTO.setEmail("contact@acme.com");
        providerDTO.setPhoneNumber("123456789");
        providerDTO.setTypes(Set.of("Electronics"));

        existingProvider = new Provider("Acme Corp", "Main St 1", "123456789",
                "https://acme.com", "John Doe", "contact@acme.com", Set.of("Electronics"));
        existingProvider.setId(1L);
    }

    @Test
    void create_newProvider_success() {
        when(providerRepository.existsByName("Acme Corp")).thenReturn(false);
        when(providerRepository.save(any(Provider.class))).thenReturn(existingProvider);

        Provider result = providerService.create(providerDTO);

        assertNotNull(result);
        assertEquals("Acme Corp", result.getName());
        verify(providerRepository).save(any(Provider.class));
    }

    @Test
    void create_duplicateName_throwsConflict() {
        when(providerRepository.existsByName("Acme Corp")).thenReturn(true);

        assertThrows(ConflictException.class, () -> providerService.create(providerDTO));
        verify(providerRepository, never()).save(any());
    }

    @Test
    void findById_notFound_throwsResourceNotFound() {
        when(providerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> providerService.findById(999L));
    }

    @Test
    void update_sameName_success() {
        when(providerRepository.findById(1L)).thenReturn(Optional.of(existingProvider));
        when(providerRepository.save(any(Provider.class))).thenAnswer(inv -> inv.getArgument(0));

        // DTO keeps the same name -> no uniqueness check needed
        Provider result = providerService.update(1L, providerDTO);

        assertEquals("contact@acme.com", result.getEmail());
        verify(providerRepository, never()).existsByName(anyString());
    }

    @Test
    void update_newNameAlreadyInUse_throwsConflict() {
        when(providerRepository.findById(1L)).thenReturn(Optional.of(existingProvider));
        providerDTO.setName("Globex"); // different from existing "Acme Corp"
        when(providerRepository.existsByName("Globex")).thenReturn(true);

        assertThrows(ConflictException.class, () -> providerService.update(1L, providerDTO));
        verify(providerRepository, never()).save(any());
    }

    @Test
    void delete_withAssociatedProducts_throwsConflict() {
        when(providerRepository.findById(1L)).thenReturn(Optional.of(existingProvider));
        when(productRepository.countByProvidersContaining(existingProvider)).thenReturn(3L);

        assertThrows(ConflictException.class, () -> providerService.delete(1L));
        verify(providerRepository, never()).deleteById(any());
    }

    @Test
    void delete_noAssociations_success() {
        when(providerRepository.findById(1L)).thenReturn(Optional.of(existingProvider));
        when(productRepository.countByProvidersContaining(existingProvider)).thenReturn(0L);

        assertDoesNotThrow(() -> providerService.delete(1L));
        verify(providerRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFound() {
        when(providerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> providerService.delete(999L));
        verify(providerRepository, never()).deleteById(any());
    }

    @Test
    void search_returnsPageResponse() {
        Page<Provider> page = new PageImpl<>(List.of(existingProvider), PageRequest.of(0, 10), 1);
        when(providerRepository.findByNameContainingIgnoreCase(eq("acme"), any(PageRequest.class)))
                .thenReturn(page);

        PageResponse<Provider> result = providerService.search("acme", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }
}
